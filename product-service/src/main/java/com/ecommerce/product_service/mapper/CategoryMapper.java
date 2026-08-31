package com.ecommerce.product_service.mapper;

import com.ecommerce.product_service.dto.request.CategoryCreateRequest;
import com.ecommerce.product_service.dto.request.CategoryUpdateRequest;
import com.ecommerce.product_service.dto.response.CategoryResponse;
import com.ecommerce.product_service.entity.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    public Category toEntity(CategoryCreateRequest request) {
        if (request == null) {
            return null;
        }

        return Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();
    }

    public CategoryResponse toResponse(Category category) {
        if (category == null) {
            return null;
        }

        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.getActive(),
                category.getCreatedAt(),
                category.getUpdatedAt()
        );
    }

    public void updateEntity(Category category, CategoryUpdateRequest request) {
        if (category == null || request == null) {
            return;
        }

        category.setName(request.getName());
        category.setDescription(request.getDescription());

        if (request.getActive() != null) {
            category.setActive(request.getActive());
        }
    }
}