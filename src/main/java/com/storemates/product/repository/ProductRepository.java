package com.storemates.product.repository;

import com.storemates.product.entity.ProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.math.BigDecimal;

public interface ProductRepository extends JpaRepository<ProductEntity,Long> {

    boolean existsByName(String name);



        // por subcategoría
        Page<ProductEntity> findBySubcategoryId(Long subcategoryId, Pageable pageable);

        // por categoría (de subcategoría)
        Page<ProductEntity> findBySubcategory_CategoryId(Long categoryId, Pageable pageable);


        // por rango de precios
        Page<ProductEntity> findByPriceBetween(BigDecimal min, BigDecimal max, Pageable pageable);

        // con stock mayor que
        Page<ProductEntity> findByStockGreaterThan(int stock, Pageable pageable);

        // sin stock
        Page<ProductEntity> findByStockEquals(int stock, Pageable pageable);

        // para ADMIN
        Page<ProductEntity> findByNameContainingIgnoreCase(String name, Pageable pageable);

        // para USER
        Page<ProductEntity> findByNameContainingIgnoreCaseAndStockGreaterThan(String name, Integer stock, Pageable pageable);

}
