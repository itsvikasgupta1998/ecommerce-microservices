package com.ecommerce.product_service.service.impl;

import com.ecommerce.product_service.dto.request.CategoryCreateRequest;
import com.ecommerce.product_service.dto.request.CategoryUpdateRequest;
import com.ecommerce.product_service.dto.response.CategoryResponse;
import com.ecommerce.product_service.entity.Category;
import com.ecommerce.product_service.mapper.CategoryMapper;
import com.ecommerce.product_service.repository.CategoryRepository;
import com.ecommerce.product_service.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import com.ecommerce.product_service.exception.DuplicateResourceException;
import com.ecommerce.product_service.exception.ResourceNotFoundException;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    @Transactional
    public CategoryResponse createCategory(CategoryCreateRequest request) {

        log.info("Creating category with name: {}", request.getName());

        if (categoryRepository.existsByNameIgnoreCaseAndActiveTrue(request.getName().trim())) {
            log.warn("Category already exists: {}", request.getName());
            throw new DuplicateResourceException(
                    "Category already exists with name: " + request.getName()
            );
        }

        Category category = categoryMapper.toEntity(request);

        category.setName(category.getName().trim());

        if (category.getDescription() != null) {
            category.setDescription(category.getDescription().trim());
        }

        Category savedCategory = categoryRepository.save(category);

        log.info("Category created successfully with id: {}", savedCategory.getId());

        return categoryMapper.toResponse(savedCategory);
    }

    @Override
    public CategoryResponse getCategoryById(Long id) {

        log.debug("Fetching category with id: {}", id);

        Category category = findCategoryById(id);

        return categoryMapper.toResponse(category);
    }

    @Override
    public List<CategoryResponse> getAllCategories() {

        log.debug("Fetching all categories");

        return categoryRepository.findAllByActiveTrue()
                .stream()
                .map(categoryMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public CategoryResponse updateCategory(
            Long id,
            CategoryUpdateRequest request
    ) {

        log.info("Updating category with id: {}", id);

        Category category = findCategoryById(id);

        String newName = request.getName().trim();

        if (!category.getName().equalsIgnoreCase(newName)
                && categoryRepository.existsByNameIgnoreCaseAndActiveTrueAndIdNot(
                newName, id)) {

            log.warn("Category already exists with name: {}", newName);

            throw new DuplicateResourceException(
                    "Category already exists with name: " + newName
            );
        }

        categoryMapper.updateEntity(category, request);

        category.setName(newName);

        if (category.getDescription() != null) {
            category.setDescription(category.getDescription().trim());
        }

        Category updatedCategory = categoryRepository.save(category);

        log.info("Category updated successfully with id: {}", id);

        return categoryMapper.toResponse(updatedCategory);
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {

        Category category = categoryRepository
                .findByIdAndActiveTrue(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Category not found with id: " + id
                        )
                );

        category.setActive(false);
        categoryRepository.save(category);
        log.info("Category deactivated successfully with id: {}", id);
    }

    private Category findCategoryById(Long id) {

        return categoryRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Category not found with id: {}", id);

                    return new ResourceNotFoundException(
                            "Category not found with id: " + id
                    );
                });
    }
}