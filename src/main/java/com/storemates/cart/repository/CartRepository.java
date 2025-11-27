package com.storemates.cart.repository;

import com.storemates.category.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<CategoryEntity,Long> {
}
