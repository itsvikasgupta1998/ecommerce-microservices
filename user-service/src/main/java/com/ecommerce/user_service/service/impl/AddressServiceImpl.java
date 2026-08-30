package com.ecommerce.user_service.service.impl;

import com.ecommerce.user_service.dto.request.CreateAddressRequest;
import com.ecommerce.user_service.dto.request.UpdateAddressRequest;
import com.ecommerce.user_service.dto.response.AddressResponse;
import com.ecommerce.user_service.entity.Address;
import com.ecommerce.user_service.entity.User;
import com.ecommerce.user_service.exception.AddressNotFoundException;
import com.ecommerce.user_service.exception.UserNotFoundException;
import com.ecommerce.user_service.mapper.AddressMapper;
import com.ecommerce.user_service.repository.AddressRepository;
import com.ecommerce.user_service.repository.UserRepository;
import com.ecommerce.user_service.security.CustomUserPrincipal;
import com.ecommerce.user_service.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final AddressMapper addressMapper;

    @Override
    @Transactional
    public AddressResponse addAddress(CreateAddressRequest request) {

        Long userId = getAuthenticatedUserId();
        User user = findUserById(userId);
        Address address = addressMapper.toEntity(request, user);

        if (Boolean.TRUE.equals(address.getDefaultAddress())) {
            removeExistingDefaultAddress(userId);
        }

        Address savedAddress = addressRepository.save(address);
        return addressMapper.toResponse(savedAddress);
    }

    @Override
    public AddressResponse getAddressById(Long addressId) {

        Long userId = getAuthenticatedUserId();
        Address address = findUserAddress(userId, addressId);
        return addressMapper.toResponse(address);
    }

    @Override
    public List<AddressResponse> getUserAddresses() {

        Long userId = getAuthenticatedUserId();

        return addressRepository.findAllByUserId(userId)
                .stream()
                .map(addressMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public AddressResponse updateAddress(
            Long addressId,
            UpdateAddressRequest request
    ) {

        Long userId = getAuthenticatedUserId();
        Address address = findUserAddress(userId, addressId);
        addressMapper.updateEntity(address, request);

        if (Boolean.TRUE.equals(address.getDefaultAddress())) {
            removeExistingDefaultAddress(userId, addressId);
        }

        Address updatedAddress = addressRepository.save(address);
        return addressMapper.toResponse(updatedAddress);
    }

    @Override
    @Transactional
    public void deleteAddress(
            Long addressId
    ) {

        Long userId = getAuthenticatedUserId();
        Address address = findUserAddress(userId, addressId);
        addressRepository.delete(address);
    }

    @Override
    @Transactional
    public void setDefaultAddress(Long addressId) {

        Long userId = getAuthenticatedUserId();
        Address address = findUserAddress(userId, addressId);
        removeExistingDefaultAddress(userId, addressId);
        address.setDefaultAddress(true);
        addressRepository.save(address);
    }


    private Long getAuthenticatedUserId() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null
                || !(authentication.getPrincipal()
                instanceof CustomUserPrincipal principal)) {

            throw new UserNotFoundException(
                    "Authenticated user not found"
            );
        }

        return principal.getUserId();
    }

    private User findUserById(Long userId) {

        return userRepository.findById(userId)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "User not found with id: " + userId
                        )
                );
    }

    private Address findUserAddress(
            Long userId,
            Long addressId
    ) {

        return addressRepository
                .findByIdAndUserId(addressId, userId)
                .orElseThrow(() ->
                        new AddressNotFoundException(
                                "Address not found with id: " + addressId
                        )
                );
    }

    private void removeExistingDefaultAddress(
            Long userId
    ) {

        addressRepository
                .findByUserIdAndDefaultAddressTrue(userId)
                .ifPresent(address -> {
                    address.setDefaultAddress(false);
                    addressRepository.save(address);
                });
    }

    private void removeExistingDefaultAddress(
            Long userId,
            Long addressId
    ) {

        addressRepository
                .findByUserIdAndDefaultAddressTrue(userId)
                .ifPresent(address -> {

                    if (!address.getId().equals(addressId)) {
                        address.setDefaultAddress(false);
                        addressRepository.save(address);
                    }
                });
    }
}