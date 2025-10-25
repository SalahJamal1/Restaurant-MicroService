package com.order.app.order;

import com.fasterxml.jackson.databind.JsonNode;
import com.order.app.cart.Cart;
import com.order.app.configuration.MapperConfiguration;
import com.order.app.order.dto.OrderDto;
import com.order.app.services.AuthServiceClient;
import com.order.app.services.ItemServiceClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;


@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService service;
    private final AuthServiceClient authServiceClient;
    private final ItemServiceClient itemServiceClient;
    private final MapperConfiguration mapperConfiguration;

    private JsonNode getUser(String token) {

        var user = authServiceClient.getUserIdFromToken(token);
        if (user == null) {

            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "you are not logged in");
        }

        return user;
    }

    @PostMapping
    public Order createOrder(@RequestBody Order order, @RequestHeader(name = "Authorization") String token) {

        var user = getUser(token);
        var fullName = user.get("firstName").asText() + " " + user.get("lastName").asText();
        order.setUserId(user.get("id").asInt());
        order.setCustomerName(fullName);
        order.setAddress(user.get("address").asText());
        order.setPhone(user.get("phone").asText());
        var orderPrice = order.getCarts().stream()
                .map(Cart::getTotalPrice)
                .reduce(0.0f, Float::sum);
        order.setOrderPrice(orderPrice);
        return service.createOrder(order);
    }

    @DeleteMapping("/{id}")
    public void deleteOrder(@PathVariable Integer id, @RequestHeader(name = "Authorization") String token) {
        if (token == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "you are not logged in");
        }
        service.deleteOrder(id);
    }

    @PutMapping("/{id}")
    public void updateOrder(@PathVariable Integer id) {
        service.updateOrderPaidById(id);
    }

    @GetMapping
    public List<OrderDto> getAllOrders(@RequestHeader(name = "Authorization") String token) {
        var userId = getUser(token).get("id").asInt();

        var orders = service.findAllByUserId(userId);

        if (orders.isEmpty()) {
            return List.of();
        }
        var ordersDto = orders.stream().map(order -> {
            var orderDto = mapperConfiguration.toOrderDto(order);
            var cartsDto = order.getCarts().stream().map(c -> {
                var item = itemServiceClient.getItemById(c.getItemId());
                var cartDto = mapperConfiguration.toCartDto(c);
                cartDto.setItem(item);
                cartDto.setTotalPrice(c.getQuantity() * item.getUnitPrice());
                return cartDto;
            }).toList();
            orderDto.setCarts(cartsDto);
            return orderDto;
        }).toList();

        return ordersDto;
    }
}
