package com.storemates.product;

import com.storemates.category.SubcategoryEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigInteger;

@Data
@Entity
@Table(name = "products")
public class ProductEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private BigInteger price;
    private String description;
    private String url;

    @NotNull
    @Column(nullable = false)
    private int stock;

    // PRODUCTO pertenece a UNA subcategoría
    @ManyToOne
    @JoinColumn(name = "subcategory_id")
    private SubcategoryEntity subcategory;
}
