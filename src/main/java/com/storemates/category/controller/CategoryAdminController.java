package com.storemates.category.controller;

import com.storemates.category.dto.CategoryPatchRequestDTO;
import com.storemates.category.dto.CategoryRequestDTO;
import com.storemates.category.dto.CategoryResponseDTO;
import com.storemates.category.service.CategoryServiceImp;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
@Hidden
public class CategoryAdminController {
    private final CategoryServiceImp categoryService;

    @PostMapping
    public ResponseEntity<CategoryResponseDTO> create(@Valid @RequestBody CategoryRequestDTO request){
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.createCategory(request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> update(@PathVariable Long id,
                                                      @RequestBody CategoryPatchRequestDTO request){
        return ResponseEntity.ok(categoryService.updateCategory(id,request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
