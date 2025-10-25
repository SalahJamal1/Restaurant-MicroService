package com.order.app.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface OrdersRepository extends JpaRepository<Order, Integer> {
    List<Order> findAllByUserId(Integer userId);

    @Transactional
    @Modifying
    @Query("""
            UPDATE  Order o set o.status='DELIVERED' WHERE o.status='PENDING' AND o.actualDelivery <= CURRENT_TIMESTAMP AND o.paid=true AND o.userId=:userId
            """)
    int updateStatusByUserId(Integer userId);


}
