package com.storemates.order;

import com.storemates.product.ProductEntity;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "orders")
public class OrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String customerName;
    private String customerEmail;
    private String customerPhone;

    // dirección de envío
    private String shippingAddress;
    private String shippingCity;
    private String shippingZip;

    private double total;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItemEntity> items = new ArrayList<>();


    @Data
    @Entity
    @Table(name = "order_items")
    public static class OrderItemEntity {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private int quantity;
        private double price;
        private double subtotal;

        @ManyToOne
        @JoinColumn(name = "product_id")
        private ProductEntity product;

        @ManyToOne
        @JoinColumn(name = "order_id")
        private OrderEntity order;
    }
}
