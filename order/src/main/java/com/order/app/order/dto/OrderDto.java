package com.order.app.order.dto;

import com.order.app.cart.dto.CartDto;
import com.order.app.order.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderDto {
    private Integer id;
    private LocalDateTime actualDelivery;
    private LocalDateTime createdAt;
    private LocalDateTime estimatedDelivery;
    private float orderPrice;
    private Status status;
    private List<CartDto> carts;
}
