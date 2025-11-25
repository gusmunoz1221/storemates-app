package com.storemates.order;

import com.storemates.product.ProductEntity;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "order_items")
public class OrderItemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int quantity;
    private double price;
    private double subtotal;

    @ManyToOne @JoinColumn(name = "product_id")
    private ProductEntity product;
    @ManyToOne @JoinColumn(name = "order_id")
    private OrderEntity order;
}
