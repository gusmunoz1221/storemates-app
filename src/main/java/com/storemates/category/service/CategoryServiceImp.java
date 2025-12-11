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
import com.storemates.exception.BusinessException;
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
    public CategoryResponseDTO createCategory(CategoryRequestDTO categoryRequest) {
        CategoryEntity entity = categoryMapper.toCategoryEntity(categoryRequest);
        CategoryEntity savedEntity = categoryRepository.save(entity);
        return categoryMapper.toCategoryResponse(savedEntity);
    }

    @Override
    @Transactional
    public CategoryResponseDTO updateCategory(Long id, CategoryRequestDTO categoryRequest) {
        CategoryEntity category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada ID: " + id));

        categoryMapper.updateCategoryFromDto(categoryRequest, category);

        return categoryMapper.toCategoryResponse(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        CategoryEntity category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada ID: " + id));

        if (!category.getSubcategories().isEmpty())
            throw new BusinessException("No se puede eliminar una categoría con subcategorías asociadas");

        categoryRepository.delete(category);
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
        return categoryRepository.findAllWithSubcategories().stream()
                .map(categoryMapper::toCategoryResponse)
                .toList();
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
    public SubcategorySimpleDTO createSubcategory(Long categoryId, SubcategoryRequestDTO subcategoryRequest) {
        CategoryEntity parent = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("La categoría ID " + categoryId + " no existe"));

        SubcategoryEntity sub = categoryMapper.toSubcategoryEntity(subcategoryRequest);

        sub.setCategory(parent);
        parent.getSubcategories().add(sub);

        SubcategoryEntity saved = subcategoryRepository.save(sub);
        return categoryMapper.toSubcategorySimpleDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubcategorySimpleDTO> ListSubCategoryByCategoryId(Long categoryId) {
        if(!categoryRepository.existsById(categoryId))
            throw new ResourceNotFoundException("La categoría ID " + categoryId + " no existe");

        return subcategoryRepository.findByCategory_Id(categoryId)
                .stream()
                .map(categoryMapper::toSubcategorySimpleDto)
                .toList();
    }

    @Override
    @Transactional
    public SubcategorySimpleDTO updateSubcategory(Long id, SubcategoryRequestDTO subcategoryRequest) {
        SubcategoryEntity subcategory = subcategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subcategoría no encontrada ID: " + id));

        categoryMapper.updateSubcategoryFromDto(subcategoryRequest, subcategory);

        return categoryMapper.toSubcategorySimpleDto(subcategoryRepository.save(subcategory));
    }

    @Override
    @Transactional
    public void deleteSubcategory(Long id) {
        SubcategoryEntity sub = subcategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subcategoría no encontrada ID: " + id));

        if (!sub.getProducts().isEmpty())
            throw new BusinessException("La subcategoría tiene productos asociados y no puede eliminarse");

        CategoryEntity parent = sub.getCategory();
        parent.getSubcategories().remove(sub);

        subcategoryRepository.delete(sub);
    }
}
