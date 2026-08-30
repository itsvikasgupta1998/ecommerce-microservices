package com.ecommerce.user_service.dto.response;

import com.ecommerce.user_service.dto.UserResponse;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {

    private UserResponse user;

    private TokenResponse tokens;
}