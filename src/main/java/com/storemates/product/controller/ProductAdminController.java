package com.storemates.product.controller;

import com.storemates.product.dto.ProductRequestDTO;
import com.storemates.product.dto.ProductResponseDTO;
import com.storemates.product.service.ProductServiceImp;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/products")
@AllArgsConstructor
public class ProductAdminController {
    private final ProductServiceImp productService;

    @PostMapping
    public ResponseEntity<ProductResponseDTO> create(@Valid @RequestBody ProductRequestDTO requestDTO) {
       return ResponseEntity.status(HttpStatus.CREATED).body(productService.save(requestDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequestDTO requestDTO) {
        return ResponseEntity.ok((productService.update(requestDTO, id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // TABLERO PRINCIPAL (DASHBOARD)
    @GetMapping
    public ResponseEntity<Page<ProductResponseDTO>> getAllForAdmin(
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        return ResponseEntity.ok(productService.listAll(pageable));
    }

    // REPORTE DE STOCK CRÍTICO
    @GetMapping("/out-of-stock")
    public ResponseEntity<Page<ProductResponseDTO>> getOutOfStock(@PageableDefault(size = 10, sort = "name") Pageable pageable) {
        return ResponseEntity.ok(productService.listOutOfStock(pageable));
    }

    // BUSCADOR INTERNO
    @GetMapping("/search")
    public ResponseEntity<Page<ProductResponseDTO>> searchForAdmin(@RequestParam String name,
                                                                   @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(productService.searchAnyByName(name, pageable));
    }
}
