package com.ecommerce.user_service.mapper;

import com.ecommerce.user_service.dto.response.AddressResponse;
import com.ecommerce.user_service.dto.request.CreateAddressRequest;
import com.ecommerce.user_service.dto.request.UpdateAddressRequest;
import com.ecommerce.user_service.entity.Address;
import com.ecommerce.user_service.entity.User;
import org.springframework.stereotype.Component;

@Component
public class AddressMapper {

    public Address toEntity(
            CreateAddressRequest request,
            User user
    ) {

        if (request == null) {
            return null;
        }

        return Address.builder()
                .user(user)
                .recipientName(request.getRecipientName())
                .phoneNumber(request.getPhoneNumber())
                .addressLine1(request.getAddressLine1())
                .addressLine2(request.getAddressLine2())
                .city(request.getCity())
                .state(request.getState())
                .postalCode(request.getPostalCode())
                .country(request.getCountry())
                .addressType(request.getAddressType())
                .defaultAddress(
                        Boolean.TRUE.equals(request.getDefaultAddress())
                )
                .build();
    }

    public void updateEntity(
            Address address,
            UpdateAddressRequest request
    ) {

        if (address == null || request == null) {
            return;
        }

        address.setRecipientName(request.getRecipientName());
        address.setPhoneNumber(request.getPhoneNumber());
        address.setAddressLine1(request.getAddressLine1());
        address.setAddressLine2(request.getAddressLine2());
        address.setCity(request.getCity());
        address.setState(request.getState());
        address.setPostalCode(request.getPostalCode());
        address.setCountry(request.getCountry());
        address.setAddressType(request.getAddressType());

        if (request.getDefaultAddress() != null) {
            address.setDefaultAddress(request.getDefaultAddress());
        }
    }

    public AddressResponse toResponse(Address address) {

        if (address == null) {
            return null;
        }

        return AddressResponse.builder()
                .id(address.getId())
                .userId(address.getUser().getId())
                .recipientName(address.getRecipientName())
                .phoneNumber(address.getPhoneNumber())
                .addressLine1(address.getAddressLine1())
                .addressLine2(address.getAddressLine2())
                .city(address.getCity())
                .state(address.getState())
                .postalCode(address.getPostalCode())
                .country(address.getCountry())
                .addressType(address.getAddressType())
                .defaultAddress(address.getDefaultAddress())
                .createdAt(address.getCreatedAt())
                .updatedAt(address.getUpdatedAt())
                .build();
    }
}