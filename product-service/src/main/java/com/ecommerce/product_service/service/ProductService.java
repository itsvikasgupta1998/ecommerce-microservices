package com.ecommerce.product_service.service;

import com.ecommerce.product_service.dto.request.ProductCreateRequest;
import com.ecommerce.product_service.dto.request.ProductUpdateRequest;
import com.ecommerce.product_service.dto.response.ProductResponse;
import org.springframework.data.domain.Page;

public interface ProductService {

    ProductResponse createProduct(ProductCreateRequest request);

    ProductResponse getProductById(Long id);

    Page<ProductResponse> getAllProducts(
            int page,
            int size,
            String sortBy,
            String sortDir,
            String search
    );

    ProductResponse updateProduct(
            Long id,
            ProductUpdateRequest request
    );

    void deleteProduct(Long id);
}