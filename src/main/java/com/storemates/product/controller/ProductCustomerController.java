package com.storemates.product.controller;

import com.storemates.product.dto.ProductResponseDTO;
import com.storemates.product.service.ProductServiceImp;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;

@RestController
@RequestMapping("/products")
@AllArgsConstructor
public class ProductCustomerController {
    private final ProductServiceImp productService;

    // CATALOGO (HOME)
    @GetMapping("/catalog")
    public ResponseEntity<Page<ProductResponseDTO>> getCatalog(
            @PageableDefault(size = 10, sort = "name") Pageable pageable){
        return ResponseEntity.ok(productService.listInStock(pageable));
    }

    // BUSCADOR
    @GetMapping("/search")
    public ResponseEntity<Page<ProductResponseDTO>> searchProducts(
            @RequestParam String name,
            @PageableDefault(size = 10, sort= "name") Pageable pageable){
        return ResponseEntity.ok(productService.searchAvailableByName(name,pageable));
    }

    // DETALLE DEL PRODUCTO
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getProduct(@PathVariable Long id){
        return ResponseEntity.ok(productService.findById(id));
    }

    // FILTRA POR CATEGORÍA
    @GetMapping("/filter/category/{categoryId}")
    public ResponseEntity<Page<ProductResponseDTO>> filterByCategory(
            @PathVariable Long categoryId,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(productService.filterByCategory(categoryId, pageable));
    }

    // FILTRA POR RANGO DE PRECIO
    @GetMapping("/filter/price")
    public ResponseEntity<Page<ProductResponseDTO>> filterByPrice(
            @RequestParam BigDecimal min,
            @RequestParam BigDecimal max,
            @PageableDefault(size = 10, sort = "price") Pageable pageable) {
        return ResponseEntity.ok(productService.filterByPriceRange(min, max, pageable));
    }
}
