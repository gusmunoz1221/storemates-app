package com.storemates.product.entity;

import com.storemates.category.entity.SubcategoryEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "products")
@Audited
public class ProductEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;
    private BigDecimal price;
    private String description;
    private String url;

    @NotNull
    @Column(nullable = false)
    private int stock;

    // PRODUCTO pertenece a UNA subcategoría
    @ManyToOne
    @JoinColumn(name = "subcategory_id")
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private SubcategoryEntity subcategory;
}
