package com.storemates.category.controller;

import com.storemates.category.dto.SubcategoryPatchRequestDTO;
import com.storemates.category.dto.SubcategoryRequestDTO;
import com.storemates.category.dto.SubcategorySimpleDTO;
import com.storemates.category.service.CategoryService;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/subcategories")
@RequiredArgsConstructor
@Hidden
public class SubCategoryAdminController {
    private final CategoryService categoryService;

    @PostMapping("/{id}")
    public ResponseEntity<SubcategorySimpleDTO> create(@PathVariable Long categoryId,
                                                        @Valid @RequestBody SubcategoryRequestDTO request){
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(categoryService.createSubcategory(categoryId,request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SubcategorySimpleDTO> update(@PathVariable Long id,
                                                       @RequestBody SubcategoryPatchRequestDTO request){
        return ResponseEntity.ok(categoryService.updateSubcategory(id,request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        categoryService.deleteSubcategory(id);
        return ResponseEntity.noContent().build();
    }

}
