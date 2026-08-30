package com.ecommerce.user_service.service;

import com.ecommerce.user_service.dto.UserResponse;
import com.ecommerce.user_service.dto.request.UserCreateRequest;
import com.ecommerce.user_service.dto.request.UserUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

    UserResponse createUser(UserCreateRequest request);

    UserResponse getUserById(Long userId);

    Page<UserResponse> getAllUsers(Pageable pageable);

    UserResponse updateUser(Long userId, UserUpdateRequest request);

    void activateUser(Long userId);

    void deactivateUser(Long userId);
}