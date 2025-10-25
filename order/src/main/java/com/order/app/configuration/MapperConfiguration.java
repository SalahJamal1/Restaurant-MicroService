package com.order.app.configuration;

import com.order.app.cart.Cart;
import com.order.app.cart.dto.CartDto;
import com.order.app.order.Order;
import com.order.app.order.dto.OrderDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MapperConfiguration {

    OrderDto toOrderDto(Order order);
    
    CartDto toCartDto(Cart cart);

}
