package com.ecommerce.user_service.dto.response;

import com.ecommerce.user_service.enums.AddressType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressResponse {

    private Long id;

    private Long userId;

    private String recipientName;

    private String phoneNumber;

    private String addressLine1;

    private String addressLine2;

    private String city;

    private String state;

    private String postalCode;

    private String country;

    private AddressType addressType;

    private Boolean defaultAddress;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}