package com.ecommerce.user_service.controller;

import com.ecommerce.user_service.dto.UserResponse;
import com.ecommerce.user_service.dto.request.UserCreateRequest;
import com.ecommerce.user_service.dto.request.UserUpdateRequest;
import com.ecommerce.user_service.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponse> createUser(
            @Valid @RequestBody UserCreateRequest request
    ) {

        UserResponse response = userService.createUser(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN') or authentication.principal.userId == #userId")
    public ResponseEntity<UserResponse> getUserById(
            @PathVariable Long userId
    ) {

        UserResponse response = userService.getUserById(userId);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserResponse>> getAllUsers(
            @PageableDefault(size = 20, sort = "createdAt")
            Pageable pageable
    ) {

        Page<UserResponse> response = userService.getAllUsers(pageable);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN') or authentication.principal.userId == #userId")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long userId,
            @Valid @RequestBody UserUpdateRequest request
    ) {

        UserResponse response = userService.updateUser(userId, request);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{userId}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deactivateUser(
            @PathVariable Long userId
    ) {

        userService.deactivateUser(userId);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{userId}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> activateUser(
            @PathVariable Long userId
    ) {

        userService.activateUser(userId);

        return ResponseEntity.noContent().build();
    }

}