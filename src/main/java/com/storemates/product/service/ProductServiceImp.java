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
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;

@Service
@AllArgsConstructor
public class ProductServiceImp implements ProductService {
    private final SubcategoryRepository subcategoryRepository;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;


    /**
     *  -crea un nuevo producto a partir del DTO recibido
     *  -valida que la subcategoría exista y que no exista otro producto con el mismo nombre
     *  -persiste el producto y retorna su DTO
     *  -lanza ResourceNotFoundException si la cateogoria no se encontro
     *  -lanza BusinessException si el nombre del producto ya existe
     */
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

    /**
     *  -actualiza un producto existente con los datos del DTO
     *  -valida que no exista otro producto con el mismo nombre
     *  -si cambia la subcategoría, valida que la nueva exista
     *  -persiste los cambios en la entidad y retorna el producto actualizado en un DTO
     */
    @Transactional
    @Override
    public ProductResponseDTO update(ProductRequestDTO productRequest, Long productId) {

        ProductEntity product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("El producto con ID: " + productId + " no existe"));

        if (!product.getName().equalsIgnoreCase(productRequest.getName()) &&
                productRepository.existsByName(productRequest.getName()))
            throw new BusinessException("producto con el nombre: "+productRequest.getName()+" ya existe");

        SubcategoryEntity subcategory = product.getSubcategory();
        if (productRequest.getSubcategoryId() != null &&
                !productRequest.getSubcategoryId().equals(product.getSubcategory().getId()))
            subcategory = subcategoryRepository.findById(productRequest.getSubcategoryId())
                    .orElseThrow(() ->
                                    new ResourceNotFoundException("Subcategoría no encontrada id: "+ productRequest.getSubcategoryId()));

        productMapper.updateEntity(product, productRequest, subcategory);

        return productMapper.entityToDto(product);
    }

    /**
     *  -elimina un producto por su ID
     *  -lanza ResourceNotFoundException si el producto no existe
     */
    @Override
    public void delete(Long id) {
        ProductEntity product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("el producto con ID: "+id+" no existe"));

        productRepository.delete(product);
    }


                /*----------------------FILTROS DE BUSCQUEDA----------------*/

    /**
     *  -retorna una página de productos aplicando la paginación y orden recibidos
     *  -convierte las entidades a DTO mediante el mapper
     */
    @Override
    public Page<ProductResponseDTO> listAll(Pageable pageable) {
        return productRepository
                .findAll(pageable)
                .map(productMapper::entityToDto);

    }

    /**
     *  -filtra los productos por subcategoría
     *  -aplica la paginación indicada
     *  -retorna los resultados en un Page de DTO
     */
    @Override
    public Page<ProductResponseDTO> filterBySubcategory(Long subcategoryId, Pageable pageable) {
        return productRepository
                .findBySubcategoryId(subcategoryId, pageable)
                .map(productMapper::entityToDto);
    }

    /**
     *  -filtra los productos por categoría
     *  -aplica paginación
     *  -retorna los resultados en un Page de DTO
     */
    @Override
    public Page<ProductResponseDTO> filterByCategory(Long categoryId, Pageable pageable) {
        return productRepository
                .findBySubcategory_CategoryId(categoryId, pageable)
                .map(productMapper::entityToDto);
    }

    /**
     * -filtra los productos cuyo precio esté dentro del rango indicado
     * -aplica la paginación proporcionada
     * -retorna los resultados en un Page de DTO
     */
    @Override
    public Page<ProductResponseDTO> filterByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        return productRepository
                .findByPriceBetween(minPrice, maxPrice, pageable)
                .map(productMapper::entityToDto);
    }

    /**
     *  -lista solo los productos con stock mayor al valor indicado
     *  -aplica la paginación proporcionada
     *  -retorna los resultados en un Page de DTO
     */
    @Override
    public Page<ProductResponseDTO> listInStock(Pageable pageable) {
        return productRepository
                .findByStockGreaterThan(0, pageable)
                .map(productMapper::entityToDto);
    }

    /**
     *  -lista solo productos SIN stock
     *  -aplica la paginación indicada
     *  -retorna los resultados en un Page de DTO
     */
    @Override
    public Page<ProductResponseDTO> listOutOfStock(Pageable pageable) {
        return productRepository
                .findByStockEquals(0, pageable)
                .map(productMapper::entityToDto);
    }

    /**
     *  -busca productos cuyo nombre contenga el texto indicado
     *  -trae productos con o sin stock
     *  -aplica la paginación proporcionada
     *  -retorna los resultados en un Page de DTO
     *  -ADMIN SEARCH-
     */
    @Override
    public Page<ProductResponseDTO> searchAnyByName(String name, Pageable pageable) {
        return productRepository
                .findByNameContainingIgnoreCase(name, pageable)
                .map(productMapper::entityToDto);
    }
    /**
     *  -busca productos cuyo nombre contenga el texto indicado
     *  -filtra solo los productos con stock disponible
     *  -aplica la paginación proporcionada
     *  -retorna los resultados en un Page de DTO
     *  -USER SEARCH-
     */
    @Override
    public Page<ProductResponseDTO> searchAvailableByName(String name, Pageable pageable) {
        return productRepository
                .findByNameContainingIgnoreCaseAndStockGreaterThan(name, 0, pageable)
                .map(productMapper::entityToDto);
    }
}