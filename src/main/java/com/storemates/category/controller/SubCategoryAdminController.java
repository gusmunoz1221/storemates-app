package com.storemates.category.controller;

import com.storemates.category.dto.SubcategoryRequestDTO;
import com.storemates.category.dto.SubcategorySimpleDTO;
import com.storemates.category.service.CategoryService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/subcategories")
@AllArgsConstructor
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
                                                       @RequestBody SubcategoryRequestDTO request){
        return ResponseEntity.ok(categoryService.updateSubcategory(id,request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        categoryService.deleteSubcategory(id);
        return ResponseEntity.noContent().build();
    }

}
