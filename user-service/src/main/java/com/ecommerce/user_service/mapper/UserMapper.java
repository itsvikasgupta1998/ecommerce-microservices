package com.ecommerce.user_service.mapper;

import com.ecommerce.user_service.dto.UserResponse;
import com.ecommerce.user_service.dto.request.UserCreateRequest;
import com.ecommerce.user_service.dto.request.UserUpdateRequest;
import com.ecommerce.user_service.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toEntity(UserCreateRequest request) {

        if (request == null) {
            return null;
        }

        return User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(request.getPassword())
                .build();
    }

    public void updateEntity(User user, UserUpdateRequest request) {

        if (user == null || request == null) {
            return;
        }

        user.setName(request.getName());
        user.setEmail(request.getEmail());
    }

    public UserResponse toResponse(User user) {

        if (user == null) {
            return null;
        }

        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(String.valueOf(user.getRole()))
                .active(user.getActive())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}