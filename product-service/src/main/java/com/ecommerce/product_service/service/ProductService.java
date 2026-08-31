package com.ecommerce.product_service.service;

import com.ecommerce.product_service.dto.request.ProductCreateRequest;
import com.ecommerce.product_service.dto.request.ProductUpdateRequest;
import com.ecommerce.product_service.dto.response.ProductResponse;

import java.util.List;

public interface ProductService {

    ProductResponse createProduct(ProductCreateRequest request);
    ProductResponse getProductById(Long id);
    List<ProductResponse> getAllProducts();
    ProductResponse updateProduct(Long id, ProductUpdateRequest request);
    void deleteProduct(Long id);
}