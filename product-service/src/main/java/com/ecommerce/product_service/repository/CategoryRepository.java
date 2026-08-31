package com.ecommerce.product_service.repository;

import com.ecommerce.product_service.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByIdAndActiveTrue(Long id);
    List<Category> findAllByActiveTrue();
    boolean existsByNameIgnoreCaseAndActiveTrue(String name);
    boolean existsByNameIgnoreCaseAndActiveTrueAndIdNot(String name, Long id);

}