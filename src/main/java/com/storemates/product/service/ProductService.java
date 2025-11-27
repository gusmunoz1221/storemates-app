package com.storemates.product.service;

import com.storemates.product.dto.ProductRequestDTO;
import com.storemates.product.dto.ProductResponseDTO;
import com.storemates.product.entity.ProductEntity;
import org.springframework.data.domain.Page;

import java.awt.print.Pageable;
import java.math.BigInteger;

public interface ProductService {
    // Crear producto
    ProductResponseDTO save(ProductRequestDTO product);

    // actualizar producto
    ProductResponseDTO update(ProductRequestDTO product,Long ProductId);

    // Eliminar producto
    void delete(Long id);

    // Lista los productos con paginación
    Page<ProductResponseDTO> listAll(Pageable pageable);

    // Busca por nombre
    Page<ProductResponseDTO> searchByName(String name, Pageable pageable);

    // Filtra por subcategoría
    Page<ProductResponseDTO> filterBySubcategory(Long subcategoryId, Pageable pageable);

    // Filtra por categoría (a través de la subcategoría)
    Page<ProductResponseDTO> filterByCategory(Long categoryId, Pageable pageable);

    // Filtra por precios
    Page<ProductResponseDTO> filterByPriceRange(BigInteger minPrice, BigInteger maxPrice, Pageable pageable);

    // Lista los productos con stock mayor que cero
    Page<ProductResponseDTO> listInStock(Pageable pageable);
}
