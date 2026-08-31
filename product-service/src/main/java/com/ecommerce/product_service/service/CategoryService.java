package com.ecommerce.product_service.service;

import com.ecommerce.product_service.dto.request.CategoryCreateRequest;
import com.ecommerce.product_service.dto.request.CategoryUpdateRequest;
import com.ecommerce.product_service.dto.response.CategoryResponse;

import java.util.List;

public interface CategoryService {

    CategoryResponse createCategory(CategoryCreateRequest request);

    CategoryResponse getCategoryById(Long id);

    List<CategoryResponse> getAllCategories();

    CategoryResponse updateCategory(Long id, CategoryUpdateRequest request);

    void deleteCategory(Long id);
}