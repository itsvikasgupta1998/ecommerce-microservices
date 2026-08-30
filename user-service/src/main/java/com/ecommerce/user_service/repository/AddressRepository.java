package com.ecommerce.user_service.repository;

import com.ecommerce.user_service.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address, Long> {

    List<Address> findAllByUserId(Long userId);

    Optional<Address> findByIdAndUserId(Long id, Long userId);

    Optional<Address> findByUserIdAndDefaultAddressTrue(Long userId);

    boolean existsByIdAndUserId(Long id, Long userId);

    boolean existsByUserIdAndDefaultAddressTrue(Long userId);
}