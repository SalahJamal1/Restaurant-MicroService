package com.order.app.cart.dto;

import com.order.app.cart.Item;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartDto {
    private Integer id;
    private Integer quantity;
    private float totalPrice;
    private Item item;
}
