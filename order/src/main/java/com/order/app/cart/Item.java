package com.order.app.cart;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Item {
    private Integer id;
    private String description;
    private String imageUrl;
    private String name;
    private float unitPrice;
}
