package com.order.app.order;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrdersRepository repository;

    @Transactional
    public Order createOrder(Order order) {
        return repository.save(order);
    }

    @Transactional
    public void deleteOrder(Integer orderId) {
        Order order = findById(orderId);
        repository.delete(order);
    }

    @Transactional
    public List<Order> findAllByUserId(Integer userId) {
        repository.updateStatusByUserId(userId);
        return repository.findAllByUserId(userId);

    }

    public Order findById(Integer id) {
        return repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

    }

    @Transactional
    public void updateOrderPaidById(Integer orderId) {
        Order order = findById(orderId);
        order.setPaid(true);
        repository.save(order);

    }
}
