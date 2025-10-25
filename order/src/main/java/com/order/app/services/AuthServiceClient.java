package com.order.app.services;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "auth-service", url = "http://auth-service:8081")
public interface AuthServiceClient {
    @GetMapping("/api/v1/auth/user-token")
    JsonNode getUserIdFromToken(@RequestHeader(name = "Authorization") String token);

}
