package com.order.app.order;

import com.order.app.cart.Cart;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private LocalDateTime actualDelivery;
    @CreationTimestamp
    private LocalDateTime createdAt;
    private LocalDateTime estimatedDelivery;
    private String address;
    private String customerName;
    private float orderPrice;
    private String phone;
    @Enumerated(EnumType.STRING)
    private Status status;
    private Integer userId;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "order_id")
    private List<Cart> carts;
    private boolean paid = false;


    @PrePersist
    public void prePersist() {
        this.actualDelivery = LocalDateTime.now().plusMinutes(15);
        this.estimatedDelivery = LocalDateTime.now().plusMinutes(25);
        this.status = Status.PENDING;

    }
}
