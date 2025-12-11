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
@Transactional(readOnly = true)
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
    public ProductResponseDTO createProduct(ProductRequestDTO productRequest) {
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
    public ProductResponseDTO updateProduct(ProductRequestDTO request, Long productId) {

        ProductEntity product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("El producto con ID: " + productId + " no existe"));

        if (!product.getName().equalsIgnoreCase(request.getName()) &&
                productRepository.existsByName(request.getName()))
            throw new BusinessException("producto con el nombre: "+request.getName()+" ya existe");

        SubcategoryEntity subcategory = product.getSubcategory();
        if (request.getSubcategoryId() != null &&
                !request.getSubcategoryId().equals(product.getSubcategory().getId()))
            subcategory = subcategoryRepository.findById(request.getSubcategoryId())
                    .orElseThrow(() ->
                                    new ResourceNotFoundException("Subcategoría no encontrada id: "+ request.getSubcategoryId()));

        if (request.getStock() < 0)
            throw new BusinessException("El stock no puede ser negativo");

        if (request.getPrice().compareTo(BigDecimal.ZERO) < 0)
            throw new BusinessException("El precio no puede ser negativo");

        productMapper.updateEntity(product, request, subcategory);

        return productMapper.entityToDto(product);
    }

    /**
     *  -elimina un producto por su ID
     *  -lanza ResourceNotFoundException si el producto no existe
     */
    @Override
    public void deleteProduct(Long id) {
        ProductEntity product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("el producto con ID: "+id+" no existe"));

        productRepository.delete(product);
    }

    /**
     *  -retorna los detalles del producto
     *  -cuando un customer hace click en la foto
     * */
    public ProductResponseDTO findById(Long id){
        return productRepository.findById(id)
                .map(productMapper::entityToDto)
                .orElseThrow(() -> new ResourceNotFoundException("el producto con ID: "+id+" no existe"));
    }

                /*----------------------FILTROS DE BUSCQUEDA----------------*/

    /**
     *  -retorna una página de productos aplicando la paginación y orden recibidos
     *  -convierte las entidades a DTO mediante el mapper
     *  -ADMIN-
     */
    @Override
    public Page<ProductResponseDTO> listProducts(Pageable pageable) {
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
    public Page<ProductResponseDTO> filterProductsByCategory(Long categoryId, Pageable pageable) {
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
     *  -ADMIN-
     */
    @Override
    public Page<ProductResponseDTO> listOutOfStock(Pageable pageable) {
        return productRepository
                .findByStockEquals(0, pageable)
                .map(productMapper::entityToDto);
    }

    /**
     * - busca productos que contengan CUALQUIERA de las palabras indicadas (Lógica OR)
     * - normaliza la entrada: convierte espacios múltiples en el operador regex pipe (|)
     * - trae productos CON o SIN stock (Visión Global)
     * - retorna los resultados en un Page de DTO
     * - ADMIN SEARCH-
     */
    @Override
    public Page<ProductResponseDTO> searchProducts(String name, Pageable pageable) {
        if (name == null || name.isBlank()) return Page.empty();

        String regex = name.trim().replaceAll("\\s+", "|");

        return productRepository
                .searchByNameRegexAny(regex, pageable)
                .map(productMapper::entityToDto);
    }

    /**
     * - busca productos que contengan CUALQUIERA de las palabras indicadas (Lógica OR)
     * - normaliza la entrada: convierte espacios múltiples en el operador regex pipe (|)
     * - filtra SOLO los productos con stock disponible (> 0)
     * - retorna los resultados en un Page de DTO
     * -USER SEARCH-
     */
    @Override
    public Page<ProductResponseDTO> searchAvailableProducts(String name, Pageable pageable) {
        if (name == null || name.isBlank()) return Page.empty();

        String regex = name.trim().replaceAll("\\s+", "|");

        return productRepository
                .searchByNameRegexAvailable(regex, pageable)
                .map(productMapper::entityToDto);
    }
}