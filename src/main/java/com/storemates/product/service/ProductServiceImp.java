package com.storemates.product.service;

import com.storemates.category.entity.SubcategoryEntity;
import com.storemates.category.repository.SubcategoryRepository;
import com.storemates.exception.BusinessException;
import com.storemates.exception.ResourceNotFoundException;
import com.storemates.product.dto.ProductRequestDTO;
import com.storemates.product.dto.ProductResponseDTO;
import com.storemates.product.entity.ProductEntity;
import com.storemates.product.mapper.ProductMapper;
import com.storemates.product.repository.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.print.Pageable;
import java.math.BigInteger;

@Service
@AllArgsConstructor
public class ProductServiceImp implements ProductService {
    private final SubcategoryRepository subcategoryRepository;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;


    /* - busco la subcategoria, si no existe lanzo una exception
    *  - si el name product existe en la bdd lanzo una exception
    *  - mapeo el DTO a entity y lo persisto en la BD
    *  - mapeo el entity a DTO y lo retorno
    * */
    @Transactional
    @Override
    public ProductResponseDTO save(ProductRequestDTO productRequest) {
        SubcategoryEntity subcategory = subcategoryRepository
                .findById(productRequest.getSubcategoryId())
                .orElseThrow(()-> new ResourceNotFoundException("subcategoria no encontrada"));

        if(productRepository.existsByName(productRequest.getName()))
            throw new BusinessException("producto con el nombre: "+productRequest.getName()+" ya existe");

        ProductEntity product = productMapper.dtoToEntity(productRequest,subcategory);
        productRepository.save(product);

        return  productMapper.entityToDto(product);
    }

    /* - busco el producto
    *  - busco la categoria si viene en el DTO
    *  - persisto los nuevos valores del DTO al producto en el mapper
    *  - mapeo el entity a DTO y lo retorno
    * */
    @Transactional
    @Override
    public ProductResponseDTO update(ProductRequestDTO productRequest, Long productId) {
        SubcategoryEntity subcategory = null;

        ProductEntity product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("El producto con id " + productId + " no existe"));

        if (productRequest.getSubcategoryId() != null)
            subcategory = subcategoryRepository.findById(productRequest.getSubcategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Subcategoría no encontrada (id: " + productRequest.getSubcategoryId() + ")"));

        productMapper.updateEntity(product, productRequest, subcategory);

        return productMapper.entityToDto(product);
    }

    @Override
    public void delete(Long id) {

    }

    @Override
    public Page<ProductResponseDTO> listAll(Pageable pageable) {
        return null;
    }

    @Override
    public Page<ProductResponseDTO> searchByName(String name, Pageable pageable) {
        return null;
    }

    @Override
    public Page<ProductResponseDTO> filterBySubcategory(Long subcategoryId, Pageable pageable) {
        return null;
    }

    @Override
    public Page<ProductResponseDTO> filterByCategory(Long categoryId, Pageable pageable) {
        return null;
    }

    @Override
    public Page<ProductResponseDTO> filterByPriceRange(BigInteger minPrice, BigInteger maxPrice, Pageable pageable) {
        return null;
    }

    @Override
    public Page<ProductResponseDTO> listInStock(Pageable pageable) {
        return null;
    }

    @Override
    public Page<ProductResponseDTO> listOutOfStock(Pageable pageable) {
        return null;
    }
}
