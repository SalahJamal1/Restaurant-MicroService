package com.payment.app.services;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "order-service", url = "order-service:8083/api/v1")
public interface OrderService {


    @DeleteMapping("/orders/{id}")
    void deleteOrder(@PathVariable Integer id, @RequestHeader("Authorization") String token);

    @PutMapping("/orders/{id}")
    void updateOrder(@PathVariable Integer id);

    @PostMapping("/orders")
    Order createOrder(@RequestBody Order order, @RequestHeader("Authorization") String token);


}
