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
    /**
     *  -crea una nueva categoría a partir del DTO recibido
     *  -persiste la categoría y retorna su DTO
     */
    @Override
    @Transactional
    public CategoryResponseDTO createCategory(CategoryRequestDTO request) {
        CategoryEntity entity = categoryMapper.toCategoryEntity(request);
        CategoryEntity savedEntity = categoryRepository.save(entity);
        return categoryMapper.toCategoryResponse(savedEntity);
    }

    /**
     *  -actualiza una categoría existente
     *  -lanza ResourceNotFoundException si la categoría no existe
     */
    @Override
    @Transactional
    public CategoryResponseDTO updateCategory(Long id, CategoryRequestDTO request) {
        CategoryEntity category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada ID: " + id));

        categoryMapper.updateCategoryFromDto(request, category);

        return categoryMapper.toCategoryResponse(categoryRepository.save(category));
    }

    /**
     *  -elimina una categoría por su ID
     *  -valida que no tenga subcategorías asociadas
     *  -lanza ResourceNotFoundException si la categoría no existe
     *  -lanza BusinessException si tiene subcategorías asociadas
     */
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

    /**
     *  -retorna todas las categorías con sus subcategorías
     */
    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponseDTO> listCategoriesWithSubcategories() {
        return categoryRepository.findAllWithSubcategories().stream()
                .map(categoryMapper::toCategoryResponse)
                .toList();
    }

    /**
     *  -retorna una categoría por su ID
     *  -lanza ResourceNotFoundException si la categoría no existe
     */
    @Override
    @Transactional(readOnly = true)
    public CategoryResponseDTO listCategoryById(Long id) {
        return categoryRepository.findById(id)
                .map(categoryMapper::toCategoryResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada ID: " + id));
    }

    //    ----SUBCATEGORIAS------
    /**
     *  -crea una subcategoría asociada a una categoría existente
     *  -valida que la categoría padre exista
     *  -asocia la subcategoría a la categoría
     *  -persiste la subcategoría y retorna su DTO simple
     *  -lanza ResourceNotFoundException si la categoría no existe
     */
    @Override
    @Transactional
    public SubcategorySimpleDTO createSubcategory(Long categoryId, SubcategoryRequestDTO request) {
        CategoryEntity parent = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("La categoría ID " + categoryId + " no existe"));

        SubcategoryEntity sub = categoryMapper.toSubcategoryEntity(request);

        sub.setCategory(parent);
        parent.getSubcategories().add(sub);

        SubcategoryEntity saved = subcategoryRepository.save(sub);
        return categoryMapper.toSubcategorySimpleDto(saved);
    }
    /**
     *  -retorna las subcategorías de una categoría
     *  -valida que la categoría exista
     *  -lanza ResourceNotFoundException si la categoría no existe
     */
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

    /**
     *  -actualiza una subcategoría existente
     *  -lanza ResourceNotFoundException si la subcategoría no existe
     */
    @Override
    @Transactional
    public SubcategorySimpleDTO updateSubcategory(Long id, SubcategoryRequestDTO request) {
        SubcategoryEntity subcategory = subcategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subcategoría no encontrada ID: " + id));

        categoryMapper.updateSubcategoryFromDto(request, subcategory);

        return categoryMapper.toSubcategorySimpleDto(subcategoryRepository.save(subcategory));
    }

    /**
     *  -elimina una subcategoría por su ID
     *  -valida que no tenga productos asociados
     *  -remueve la subcategoría de la categoría padre
     *  -lanza ResourceNotFoundException si la subcategoría no existe
     *  -lanza BusinessException si tiene productos asociados
     */
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
