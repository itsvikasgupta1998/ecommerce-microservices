package com.ecommerce.user_service.service.impl;

import com.ecommerce.user_service.dto.UserResponse;
import com.ecommerce.user_service.dto.request.UserCreateRequest;
import com.ecommerce.user_service.dto.request.UserUpdateRequest;
import com.ecommerce.user_service.entity.User;
import com.ecommerce.user_service.exception.DuplicateEmailException;
import com.ecommerce.user_service.exception.UserNotFoundException;
import com.ecommerce.user_service.mapper.UserMapper;
import com.ecommerce.user_service.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserResponse userResponse;

    @BeforeEach
    void setUp() {

        user = new User();
        user.setId(1L);
        user.setName("Vikas Gupta");
        user.setEmail("vikas@example.com");
        user.setPassword("encodedPassword");
        user.setActive(true);

        userResponse = UserResponse.builder()
                .id(1L)
                .name("Vikas Gupta")
                .email("vikas@example.com")
                .build();
    }

    @Test
    void createUser_shouldCreateUserSuccessfully() {

        UserCreateRequest request = UserCreateRequest.builder()
                .name("Vikas Gupta")
                .email("vikas@example.com")
                .password("password123")
                .build();

        when(userRepository.existsByEmail(request.getEmail()))
                .thenReturn(false);

        when(userMapper.toEntity(request))
                .thenReturn(user);

        when(passwordEncoder.encode(request.getPassword()))
                .thenReturn("encodedPassword");

        when(userRepository.save(user))
                .thenReturn(user);

        when(userMapper.toResponse(user))
                .thenReturn(userResponse);

        UserResponse result =
                userService.createUser(request);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("vikas@example.com", result.getEmail());

        verify(userRepository).existsByEmail(request.getEmail());
        verify(passwordEncoder).encode(request.getPassword());
        verify(userRepository).save(user);
        verify(userMapper).toResponse(user);
    }

    @Test
    void createUser_shouldThrowException_whenEmailAlreadyExists() {

        UserCreateRequest request = UserCreateRequest.builder()
                .name("Vikas Gupta")
                .email("vikas@example.com")
                .password("password123")
                .build();

        when(userRepository.existsByEmail(request.getEmail()))
                .thenReturn(true);

        assertThrows(
                DuplicateEmailException.class,
                () -> userService.createUser(request)
        );

        verify(userRepository)
                .existsByEmail(request.getEmail());

        verify(userRepository, never()).save(any(User.class));

        verify(passwordEncoder, never())
                .encode(anyString());
    }

    @Test
    void getUserById_shouldReturnUserSuccessfully() {

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(userMapper.toResponse(user))
                .thenReturn(userResponse);

        UserResponse result =
                userService.getUserById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Vikas Gupta", result.getName());

        verify(userRepository).findById(1L);
        verify(userMapper).toResponse(user);
    }

    @Test
    void getUserById_shouldThrowException_whenUserNotFound() {

        when(userRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> userService.getUserById(99L)
        );

        verify(userRepository).findById(99L);
        verify(userMapper, never()).toResponse(any());
    }

    @Test
    void getAllUsers_shouldReturnPaginatedUsers() {

        Pageable pageable =
                PageRequest.of(0, 10);

        Page<User> userPage =
                new PageImpl<>(List.of(user));

        when(userRepository.findAll(pageable))
                .thenReturn(userPage);

        when(userMapper.toResponse(user))
                .thenReturn(userResponse);

        Page<UserResponse> result =
                userService.getAllUsers(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(
                "Vikas Gupta",
                result.getContent().get(0).getName()
        );

        verify(userRepository).findAll(pageable);
        verify(userMapper).toResponse(user);
    }

    @Test
    void updateUser_shouldUpdateUserSuccessfully() {

        UserUpdateRequest request = UserUpdateRequest.builder()
                .name("Vikas Kumar")
                .email("vikas.kumar@example.com")
                .build();

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(userRepository.existsByEmail(
                request.getEmail()))
                .thenReturn(false);

        when(userRepository.save(user))
                .thenReturn(user);

        when(userMapper.toResponse(user))
                .thenReturn(userResponse);

        UserResponse result =
                userService.updateUser(1L, request);

        assertNotNull(result);

        verify(userRepository).findById(1L);
        verify(userRepository)
                .existsByEmail(request.getEmail());

        verify(userMapper)
                .updateEntity(user, request);

        verify(userRepository).save(user);
        verify(userMapper).toResponse(user);
    }

    @Test
    void updateUser_shouldThrowException_whenEmailAlreadyExists() {

        UserUpdateRequest request = UserUpdateRequest.builder()
                .name("Vikas Kumar")
                .email("another@example.com")
                .build();

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(userRepository.existsByEmail(
                request.getEmail()))
                .thenReturn(true);

        assertThrows(
                DuplicateEmailException.class,
                () -> userService.updateUser(1L, request)
        );

        verify(userRepository)
                .existsByEmail(request.getEmail());

        verify(userMapper, never())
                .updateEntity(any(User.class), any());

        verify(userRepository, never())
                .save(any(User.class));
    }

    @Test
    void updateUser_shouldThrowException_whenUserNotFound() {

        UserUpdateRequest request = UserUpdateRequest.builder()
                .name("Vikas Kumar")
                .email("vikas.kumar@example.com")
                .build();

        when(userRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> userService.updateUser(99L, request)
        );

        verify(userRepository).findById(99L);

        verify(userMapper, never())
                .updateEntity(any(User.class), any());

        verify(userRepository, never())
                .save(any(User.class));
    }

    @Test
    void deactivateUser_shouldDeactivateUserSuccessfully() {

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        userService.deactivateUser(1L);

        assertFalse(user.getActive());

        verify(userRepository).findById(1L);
        verify(userRepository).save(user);
    }

    @Test
    void deactivateUser_shouldThrowException_whenUserNotFound() {

        when(userRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> userService.deactivateUser(99L)
        );

        verify(userRepository).findById(99L);

        verify(userRepository, never())
                .save(any(User.class));
    }

    @Test
    void activateUser_shouldActivateUserSuccessfully() {

        user.setActive(false);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        userService.activateUser(1L);

        assertTrue(user.getActive());

        verify(userRepository).findById(1L);
        verify(userRepository).save(user);
    }

    @Test
    void activateUser_shouldThrowException_whenUserNotFound() {

        when(userRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> userService.activateUser(99L)
        );

        verify(userRepository).findById(99L);

        verify(userRepository, never())
                .save(any(User.class));
    }
}