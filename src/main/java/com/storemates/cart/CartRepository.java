package com.storemates.cart;

import com.storemates.category.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<CategoryEntity,Long> {
}
