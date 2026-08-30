package com.ecommerce.user_service.service;

import com.ecommerce.user_service.dto.request.LoginRequest;
import com.ecommerce.user_service.dto.request.RefreshTokenRequest;
import com.ecommerce.user_service.dto.response.LoginResponse;
import com.ecommerce.user_service.dto.response.TokenResponse;

public interface AuthService {

    LoginResponse login(LoginRequest request);

    TokenResponse refreshToken(RefreshTokenRequest request);
}