package com.order.app.services;

import com.order.app.cart.Item;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "menu-service", url = "http://menu-service:8082")
public interface ItemServiceClient {
    @GetMapping("/api/v1/items/{id}")
    Item getItemById(@PathVariable Integer id);
}
