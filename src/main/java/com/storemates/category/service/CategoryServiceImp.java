package com.storemates.category.service;

import com.storemates.category.dto.CategoryRequestDTO;
import com.storemates.category.dto.CategoryResponseDTO;
import com.storemates.category.dto.SubcategoryRequestDTO;
import com.storemates.category.dto.SubcategorySimpleDTO;
import com.storemates.category.entity.CategoryEntity;
import com.storemates.category.entity.SubcategoryEntity;
import com.storemates.category.mapper.CategoryMapper;
import com.storemates.category.repository.CategoryRepository;
import com.storemates.category.repository.SubcategoryRepository;
import com.storemates.exception.ResourceNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CategoryServiceImp implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final SubcategoryRepository subcategoryRepository;
    private final CategoryMapper categoryMapper;

    //-----CATEGORIAS------

    @Override
    @Transactional
    public CategoryResponseDTO createCategory(CategoryRequestDTO request) {
        CategoryEntity entity = categoryMapper.toCategoryEntity(request);

        CategoryEntity savedEntity = categoryRepository.save(entity);

        return categoryMapper.toCategoryResponse(savedEntity);
    }

    @Override
    @Transactional
    public CategoryResponseDTO updateCategory(Long id, CategoryRequestDTO request) {
        CategoryEntity category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada ID: " + id));

        // 2. El Mapper se encarga de mezclar los datos nuevos con los viejos
        categoryMapper.updateCategoryFromDto(request, category);

        // 3. Guardamos
        return categoryMapper.toCategoryResponse(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id))
            throw new ResourceNotFoundException("Categoría no encontrada ID: " + id);

        categoryRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponseDTO> listCategories() {
        return categoryRepository.findAll().stream()
                .map(categoryMapper::toCategoryResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponseDTO> listCategoriesWithSubcategories() {
        return categoryRepository.findAll().stream()
                .map(categoryMapper::toCategoryResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponseDTO listCategoryById(Long id) {
        return categoryRepository.findById(id)
                .map(categoryMapper::toCategoryResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada ID: " + id));
    }

    //    ----SUBCATEGORIAS------

    @Override
    @Transactional
    public SubcategorySimpleDTO createSubcategory(Long categoryId, SubcategoryRequestDTO request) {
        CategoryEntity parentCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("La categoría ID: " + categoryId + " no existe"));

        SubcategoryEntity subcategory = categoryMapper.toSubcategoryEntity(request);

        subcategory.setCategory(parentCategory);

        return categoryMapper.toSubcategorySimpleDto(subcategoryRepository.save(subcategory));
    }

    @Override
    @Transactional
    public SubcategorySimpleDTO updateSubcategory(Long id, SubcategoryRequestDTO request) {
        SubcategoryEntity subcategory = subcategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subcategoría no encontrada ID: " + id));

        categoryMapper.updateSubcategoryFromDto(request, subcategory);

        return categoryMapper.toSubcategorySimpleDto(subcategoryRepository.save(subcategory));
    }

    @Override
    @Transactional
    public void deleteSubcategory(Long id) {
        if (!subcategoryRepository.existsById(id))
            throw new ResourceNotFoundException("Subcategoría no encontrada ID: " + id);

        subcategoryRepository.deleteById(id);
    }
}
