package com.storemates.category.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.envers.Audited;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "categories")
@Audited
public class CategoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;

    @OneToMany(mappedBy = "category")
    private List<SubcategoryEntity> subcategories = new ArrayList<>();
}
