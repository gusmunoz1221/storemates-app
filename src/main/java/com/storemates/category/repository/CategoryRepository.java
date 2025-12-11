package com.storemates.category.repository;

import com.storemates.category.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CategoryRepository extends JpaRepository<CategoryEntity,Long> {
    @Query("SELECT c FROM CategoryEntity c LEFT JOIN FETCH c.subcategories")
    List<CategoryEntity> findAllWithSubcategories();
}
