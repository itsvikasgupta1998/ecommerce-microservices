package com.ecommerce.product_service.service.impl;

import com.ecommerce.product_service.dto.request.ProductCreateRequest;
import com.ecommerce.product_service.dto.request.ProductUpdateRequest;
import com.ecommerce.product_service.dto.response.ProductResponse;
import com.ecommerce.product_service.entity.Category;
import com.ecommerce.product_service.entity.Product;
import com.ecommerce.product_service.exception.DuplicateResourceException;
import com.ecommerce.product_service.exception.ResourceNotFoundException;
import com.ecommerce.product_service.mapper.ProductMapper;
import com.ecommerce.product_service.repository.CategoryRepository;
import com.ecommerce.product_service.repository.ProductRepository;
import com.ecommerce.product_service.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;

    @Override
    @Transactional
    public ProductResponse createProduct(ProductCreateRequest request) {

        log.info("Creating product with SKU: {}", request.getSku());
        String sku = request.getSku().trim();

        if (productRepository.existsBySkuIgnoreCaseAndActiveTrue(sku)) {
            log.warn("Product already exists with SKU: {}", sku);

            throw new DuplicateResourceException(
                    "Product already exists with SKU: " + sku
            );
        }

        Category category = findActiveCategoryById(request.getCategoryId());
        Product product = productMapper.toEntity(request, category);
        product.setName(product.getName().trim());
        product.setSku(product.getSku().trim());

        if (product.getDescription() != null) {
            product.setDescription(product.getDescription().trim());
        }

        Product savedProduct = productRepository.save(product);

        log.info(
                "Product created successfully with id: {}",
                savedProduct.getId()
        );

        return productMapper.toResponse(savedProduct);
    }

    @Override
    public ProductResponse getProductById(Long id) {

        log.debug("Fetching product with id: {}", id);
        Product product = findActiveProductById(id);
        return productMapper.toResponse(product);
    }

    @Override
    public List<ProductResponse> getAllProducts() {

        log.debug("Fetching all active products");

        return productRepository.findAllByActiveTrue()
                .stream()
                .map(productMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public ProductResponse updateProduct(
            Long id,
            ProductUpdateRequest request
    ) {

        log.info("Updating product with id: {}", id);
        Product product = findActiveProductById(id);
        String newSku = request.getSku().trim();

        if (!product.getSku().equalsIgnoreCase(newSku)
                && productRepository
                .existsBySkuIgnoreCaseAndActiveTrueAndIdNot(newSku, id)) {

            log.warn("Product already exists with this SKU: {}", newSku);

            throw new DuplicateResourceException(
                    "Product already exists with SKU: " + newSku
            );
        }

        Category category = findActiveCategoryById(request.getCategoryId());
        productMapper.updateEntity(product, request, category);
        product.setName(product.getName().trim());
        product.setSku(newSku);

        if (product.getDescription() != null) {
            product.setDescription(product.getDescription().trim());
        }

        Product updatedProduct = productRepository.save(product);

        log.info(
                "Product updated successfully with id: {}",
                id
        );

        return productMapper.toResponse(updatedProduct);
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {

        Product product = findActiveProductById(id);
        product.setActive(false);
        productRepository.save(product);
        log.info(
                "Product deactivated successfully with id: {}",
                id
        );
    }

    private Product findActiveProductById(Long id) {

        return productRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> {
                    log.warn("Product not found with id: {}", id);

                    return new ResourceNotFoundException(
                            "Product not found with id: " + id
                    );
                });
    }

    private Category findActiveCategoryById(Long categoryId) {

        return categoryRepository.findByIdAndActiveTrue(categoryId)
                .orElseThrow(() -> {
                    log.warn(
                            "Category not found or inactive with id: {}",
                            categoryId
                    );

                    return new ResourceNotFoundException(
                            "Category not found with id: " + categoryId
                    );
                });
    }
}