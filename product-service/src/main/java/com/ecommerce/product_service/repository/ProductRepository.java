package com.ecommerce.product_service.repository;

import com.ecommerce.product_service.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findByIdAndActiveTrue(Long id);
    List<Product> findAllByActiveTrue();
    boolean existsBySkuIgnoreCaseAndActiveTrue(String sku);
    boolean existsBySkuIgnoreCaseAndActiveTrueAndIdNot(String sku, Long id);
}