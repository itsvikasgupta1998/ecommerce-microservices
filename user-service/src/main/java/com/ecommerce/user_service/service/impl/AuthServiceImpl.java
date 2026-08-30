package com.ecommerce.user_service.service.impl;

import com.ecommerce.user_service.dto.UserResponse;
import com.ecommerce.user_service.dto.request.LoginRequest;
import com.ecommerce.user_service.dto.request.RefreshTokenRequest;
import com.ecommerce.user_service.dto.response.LoginResponse;
import com.ecommerce.user_service.dto.response.TokenResponse;
import com.ecommerce.user_service.entity.User;
import com.ecommerce.user_service.exception.InactiveUserException;
import com.ecommerce.user_service.exception.InvalidCredentialsException;
import com.ecommerce.user_service.exception.UserNotFoundException;
import com.ecommerce.user_service.mapper.UserMapper;
import com.ecommerce.user_service.repository.UserRepository;
import com.ecommerce.user_service.security.JwtTokenProvider;
import com.ecommerce.user_service.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final JwtDecoder jwtDecoder;

    @Override
    public LoginResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new InvalidCredentialsException(
                                "Invalid email or password"
                        )
                );

        if (!Boolean.TRUE.equals(user.getActive())) {
            throw new InactiveUserException(
                    "User account is inactive"
            );
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        } catch (AuthenticationException exception) {
            throw new InvalidCredentialsException(
                    "Invalid email or password"
            );
        }

        String accessToken =
                jwtTokenProvider.generateAccessToken(user);

        String refreshToken =
                jwtTokenProvider.generateRefreshToken(user);

        TokenResponse tokenResponse = TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(900)
                .build();

        UserResponse userResponse =
                userMapper.toResponse(user);

        return LoginResponse.builder()
                .user(userResponse)
                .tokens(tokenResponse)
                .build();
    }

    @Override
    public TokenResponse refreshToken(
            RefreshTokenRequest request
    ) {

        Jwt jwt;

        try {
            jwt = jwtDecoder.decode(request.getRefreshToken());
        } catch (Exception exception) {
            throw new InvalidCredentialsException(
                    "Invalid or expired refresh token"
            );
        }

        String tokenType = jwt.getClaimAsString("tokenType");

        if (!"REFRESH".equals(tokenType)) {
            throw new InvalidCredentialsException(
                    "Invalid refresh token"
            );
        }

        long userId = extractUserId(jwt);

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "User not found with id: " + userId
                        )
                );

        if (!Boolean.TRUE.equals(user.getActive())) {
            throw new InactiveUserException(
                    "User account is inactive"
            );
        }

        String accessToken =
                jwtTokenProvider.generateAccessToken(user);

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(request.getRefreshToken())
                .tokenType("Bearer")
                .expiresIn(900)
                .build();
    }

    private long extractUserId(Jwt jwt) {

        String subject = jwt.getSubject();

        if (subject == null || subject.isBlank()) {
            throw new InvalidCredentialsException(
                    "Invalid refresh token"
            );
        }

        try {
            return Long.parseLong(subject);
        } catch (NumberFormatException exception) {
            throw new InvalidCredentialsException(
                    "Invalid refresh token"
            );
        }
    }
}