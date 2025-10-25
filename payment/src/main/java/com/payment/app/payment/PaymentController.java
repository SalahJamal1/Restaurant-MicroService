package com.payment.app.payment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payment.app.services.Cart;
import com.payment.app.services.Order;
import com.payment.app.services.OrderService;
import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class PaymentController {
    private final OrderService orderService;
    @Value("${STRIPE.KEY}")
    private String STRIPE_SECRET_KEY;


    @PostMapping("/create-session")
    public ResponseEntity<?> createSession(@RequestBody Order newOrder, @RequestHeader(name = "Authorization") String token) throws StripeException, JsonProcessingException {
        if (token == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "you are not logged in");
        }
        if (newOrder == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "please try again");
        }

        var orderPrice = newOrder.getCarts().stream()
                .map(Cart::getTotalPrice)
                .reduce(0.0f, Float::sum);
        newOrder.setOrderPrice(orderPrice);
        Order order = null;
        try {
            order = orderService.createOrder(newOrder, token);
            Stripe.apiKey = STRIPE_SECRET_KEY;
            String YOUR_DOMAIN = "http://localhost:5173";
            SessionCreateParams params = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl(YOUR_DOMAIN + "?success")
                    .setCancelUrl(YOUR_DOMAIN + "?cancel")
                    .setClientReferenceId(order.getId().toString())
                    .addLineItem(
                            SessionCreateParams.LineItem.builder()
                                    .setQuantity(1L)
                                    .setPriceData(
                                            SessionCreateParams.LineItem.PriceData.builder()
                                                    .setCurrency("usd")
                                                    .setUnitAmount((long) (orderPrice * 100))
                                                    .setProductData(
                                                            SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                    .setName("Order #" + order.getId())
                                                                    .build()
                                                    )
                                                    .build()
                                    )
                                    .build()
                    ).putMetadata("order_id", order.getId().toString())
                    .build();
            Session session = Session.create(params);
            Map<String, Object> response = new HashMap<>();
            response.put("url", session.getUrl());

            return ResponseEntity.ok(response);


        } catch (Exception e) {
            if (order != null) {
                orderService.deleteOrder(order.getId(), token);
            }
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());

        }
    }

    @PostMapping("/webhook")
    public void createSession(@RequestBody String payload, @RequestHeader(name = "Stripe-Signature") String sigHeader) throws StripeException, JsonProcessingException {
        Event event;
        String endpointSecret = "whsec_5ecfe91087bc84908f4109a771adf8dc37c9b87812973add260bf7aafed37e45";
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(payload);
        Integer orderId = null;
        JsonNode orderIdNode = root.path("data")
                .path("object");
        if (orderIdNode.has("client_reference_id")) {
            orderId = orderIdNode.path("client_reference_id").asInt();
        }

        try {

            event = Webhook.constructEvent(
                    payload, sigHeader, endpointSecret
            );

            if ("checkout.session.completed".equals(event.getType())) {
                orderService.updateOrder(orderId);
            }

        } catch (SignatureVerificationException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "⚠️ Invalid signature");
        }


    }


}
