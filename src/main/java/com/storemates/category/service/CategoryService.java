package com.storemates.category.service;

import com.storemates.category.dto.CategoryRequestDTO;
import com.storemates.category.dto.CategoryResponseDTO;
import com.storemates.category.dto.SubcategoryRequestDTO;
import com.storemates.category.dto.SubcategorySimpleDTO;
import java.util.List;

public interface CategoryService {

    // --- Category ---
    // CREAR CATEGORIA
    CategoryResponseDTO createCategory(CategoryRequestDTO request);

    // ACTUALIZAR CATEGORIA
    CategoryResponseDTO updateCategory(Long id, CategoryRequestDTO request);

    // ELIMINAR CATEGORIA
    void deleteCategory(Long id);

    // LISTA LAS CATEGORIAS
    List<CategoryResponseDTO> listCategories();

    // LISTA LAS CATEGORIAS CON SUB CATEGORIAS
    List<CategoryResponseDTO> listCategoriesWithSubcategories();

    // FILTRAR POR SUBCATEGORIA
    CategoryResponseDTO listCategoryById(Long subcategoryId);

    // -----subcategory----
    // CREAR SUBCATEGORIA
    SubcategorySimpleDTO createSubcategory(Long categoryId, SubcategoryRequestDTO request);

    // ACTUALIZAR SUBCATEGORIA
    SubcategorySimpleDTO updateSubcategory(Long id, SubcategoryRequestDTO request);

    // ELIMINAR SUBCATEGORIA
    void deleteSubcategory(Long id);
}
