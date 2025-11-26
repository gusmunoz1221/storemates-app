package com.storemates.category;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubcategoryRepository extends JpaRepository<SubcategoryEntity,Long> {
    List<SubcategoryEntity> findAll();
}
