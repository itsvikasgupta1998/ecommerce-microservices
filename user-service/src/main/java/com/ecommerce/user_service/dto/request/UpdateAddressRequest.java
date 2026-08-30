package com.ecommerce.user_service.dto.request;

import com.ecommerce.user_service.enums.AddressType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateAddressRequest {

    @NotBlank(message = "Full name is required")
    @Size(
            max = 100,
            message = "Full name must not exceed 100 characters"
    )
    private String recipientName;

    @NotBlank(message = "Phone number is required")
    @Pattern(
            regexp = "^[0-9]{10,15}$",
            message = "Phone number must contain 10 to 15 digits"
    )
    private String phoneNumber;

    @NotBlank(message = "Address line 1 is required")
    @Size(
            max = 255,
            message = "Address line 1 must not exceed 255 characters"
    )
    private String addressLine1;

    @Size(
            max = 255,
            message = "Address line 2 must not exceed 255 characters"
    )
    private String addressLine2;

    @NotBlank(message = "City is required")
    @Size(
            max = 100,
            message = "City must not exceed 100 characters"
    )
    private String city;

    @NotBlank(message = "State is required")
    @Size(
            max = 100,
            message = "State must not exceed 100 characters"
    )
    private String state;

    @NotBlank(message = "Postal code is required")
    @Size(
            max = 20,
            message = "Postal code must not exceed 20 characters"
    )
    private String postalCode;

    @NotBlank(message = "Country is required")
    @Size(
            max = 100,
            message = "Country must not exceed 100 characters"
    )
    private String country;

    @NotNull(message = "Address type is required")
    private AddressType addressType;

    private Boolean defaultAddress;
}