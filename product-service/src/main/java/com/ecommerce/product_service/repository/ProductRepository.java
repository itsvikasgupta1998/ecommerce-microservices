package com.ecommerce.product_service.repository;

import com.ecommerce.product_service.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findByIdAndActiveTrue(Long id);

    Page<Product> findAllByActiveTrue(Pageable pageable);

    boolean existsBySkuIgnoreCaseAndActiveTrue(String sku);

    boolean existsBySkuIgnoreCaseAndActiveTrueAndIdNot(
            String sku,
            Long id
    );

    Page<Product> findByActiveTrueAndNameContainingIgnoreCase(
            String name,
            Pageable pageable
    );
}