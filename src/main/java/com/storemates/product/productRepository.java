package com.storemates.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.math.BigInteger;

public interface productRepository extends JpaRepository<ProductEntity,Long> {

    // por nombre
    Page<ProductEntity> findByNameContainingIgnoreCase(String name, Pageable pageable);

    // por subcategoría
    Page<ProductEntity> findBySubcategoryId(Long subcategoryId, Pageable pageable);

    // por categoría (de subcategoría)
    Page<ProductEntity> findBySubcategoryCategoryId(Long categoryId, Pageable pageable);

    // por rango de precios
    Page<ProductEntity> findByPriceBetween(BigInteger min, BigInteger max, Pageable pageable);

    // con stock mayor que
    Page<ProductEntity> findByStockGreaterThan(int stock, Pageable pageable);

    // sin stock
    Page<ProductEntity> findByStock(int stock, Pageable pageable);

    // Ordenar por precio asc/desc (Spring lo maneja con Pageable)
    Page<ProductEntity> findAll(Pageable pageable);
}
