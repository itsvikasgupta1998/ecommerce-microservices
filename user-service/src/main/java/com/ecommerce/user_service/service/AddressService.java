package com.ecommerce.user_service.service;

import com.ecommerce.user_service.dto.request.CreateAddressRequest;
import com.ecommerce.user_service.dto.request.UpdateAddressRequest;
import com.ecommerce.user_service.dto.response.AddressResponse;
import java.util.List;

public interface AddressService {

    AddressResponse addAddress(CreateAddressRequest request);

    AddressResponse getAddressById(Long addressId);

    List<AddressResponse> getUserAddresses();

    AddressResponse updateAddress(Long addressId, UpdateAddressRequest request);

    void deleteAddress(Long addressId);

    void setDefaultAddress(Long addressId);
}