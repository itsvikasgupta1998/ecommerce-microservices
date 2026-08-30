package com.ecommerce.user_service.controller;

import com.ecommerce.user_service.dto.request.CreateAddressRequest;
import com.ecommerce.user_service.dto.request.UpdateAddressRequest;
import com.ecommerce.user_service.dto.response.AddressResponse;
import com.ecommerce.user_service.service.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @PostMapping
    public ResponseEntity<AddressResponse> addAddress(
            @Valid @RequestBody CreateAddressRequest request
    ) {

        AddressResponse response =
                addressService.addAddress(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{addressId}")
    public ResponseEntity<AddressResponse> getAddressById(
            @PathVariable Long addressId
    ) {

        AddressResponse response =
                addressService.getAddressById(addressId);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<AddressResponse>> getUserAddresses() {

        List<AddressResponse> response =
                addressService.getUserAddresses();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{addressId}")
    public ResponseEntity<AddressResponse> updateAddress(
            @PathVariable Long addressId,
            @Valid @RequestBody UpdateAddressRequest request
    ) {

        AddressResponse response =
                addressService.updateAddress(
                        addressId,
                        request
                );

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{addressId}")
    public ResponseEntity<Void> deleteAddress(
            @PathVariable Long addressId
    ) {

        addressService.deleteAddress(addressId);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{addressId}/default")
    public ResponseEntity<Void> setDefaultAddress(
            @PathVariable Long addressId
    ) {

        addressService.setDefaultAddress(addressId);

        return ResponseEntity.noContent().build();
    }
}