package com.ecommerce.user_service.service.impl;

import com.ecommerce.user_service.dto.request.CreateAddressRequest;
import com.ecommerce.user_service.dto.request.UpdateAddressRequest;
import com.ecommerce.user_service.dto.response.AddressResponse;
import com.ecommerce.user_service.entity.Address;
import com.ecommerce.user_service.entity.User;
import com.ecommerce.user_service.exception.AddressNotFoundException;
import com.ecommerce.user_service.exception.UserNotFoundException;
import com.ecommerce.user_service.mapper.AddressMapper;
import com.ecommerce.user_service.repository.AddressRepository;
import com.ecommerce.user_service.repository.UserRepository;
import com.ecommerce.user_service.security.CustomUserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddressServiceImplTest {

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AddressMapper addressMapper;

    @Mock
    private CustomUserPrincipal customUserPrincipal;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AddressServiceImpl addressService;

    private User user;
    private Address address;
    private Address existingDefaultAddress;
    private AddressResponse addressResponse;

    private CreateAddressRequest createRequest;
    private UpdateAddressRequest updateRequest;

    private final Long USER_ID = 1L;
    private final Long ADDRESS_ID = 10L;

    @BeforeEach
    void setUp() {

        user = new User();
        user.setId(USER_ID);

        address = new Address();
        address.setId(ADDRESS_ID);
        address.setUser(user);
        address.setDefaultAddress(false);

        existingDefaultAddress = new Address();
        Long EXISTING_ADDRESS_ID = 20L;
        existingDefaultAddress.setId(EXISTING_ADDRESS_ID);
        existingDefaultAddress.setUser(user);
        existingDefaultAddress.setDefaultAddress(true);

        addressResponse = new AddressResponse();

        createRequest = new CreateAddressRequest();

        updateRequest = new UpdateAddressRequest();

        customUserPrincipal = mock(CustomUserPrincipal.class);
        authentication = mock(Authentication.class);

        SecurityContextHolder.clearContext();
    }

    // ============================================================
    // Helper
    // ============================================================

    private void mockAuthenticatedUser() {

        when(customUserPrincipal.getUserId())
                .thenReturn(USER_ID);

        when(authentication.getPrincipal())
                .thenReturn(customUserPrincipal);

        SecurityContextHolder.getContext()
                .setAuthentication(authentication);
    }

    // ============================================================
    // ADD ADDRESS
    // ============================================================

    @Test
    void addAddress_shouldCreateAddressSuccessfully() {

        mockAuthenticatedUser();

        address.setDefaultAddress(false);

        when(userRepository.findById(USER_ID))
                .thenReturn(Optional.of(user));

        when(addressMapper.toEntity(createRequest, user))
                .thenReturn(address);

        when(addressRepository.save(address))
                .thenReturn(address);

        when(addressMapper.toResponse(address))
                .thenReturn(addressResponse);

        AddressResponse result =
                addressService.addAddress(createRequest);

        assertNotNull(result);
        assertSame(addressResponse, result);

        verify(userRepository)
                .findById(USER_ID);

        verify(addressMapper)
                .toEntity(createRequest, user);

        verify(addressRepository)
                .save(address);

        verify(addressMapper)
                .toResponse(address);

        verify(addressRepository, never())
                .findByUserIdAndDefaultAddressTrue(USER_ID);
    }

    @Test
    void addAddress_shouldRemoveExistingDefaultAddress() {

        mockAuthenticatedUser();

        address.setDefaultAddress(true);

        when(userRepository.findById(USER_ID))
                .thenReturn(Optional.of(user));

        when(addressMapper.toEntity(createRequest, user))
                .thenReturn(address);

        when(addressRepository.findByUserIdAndDefaultAddressTrue(USER_ID))
                .thenReturn(Optional.of(existingDefaultAddress));

        when(addressRepository.save(existingDefaultAddress))
                .thenReturn(existingDefaultAddress);

        when(addressRepository.save(address))
                .thenReturn(address);

        when(addressMapper.toResponse(address))
                .thenReturn(addressResponse);

        AddressResponse result =
                addressService.addAddress(createRequest);

        assertNotNull(result);
        assertSame(addressResponse, result);

        assertFalse(
                existingDefaultAddress.getDefaultAddress()
        );

        verify(addressRepository)
                .findByUserIdAndDefaultAddressTrue(USER_ID);

        verify(addressRepository)
                .save(existingDefaultAddress);

        verify(addressRepository)
                .save(address);

        verify(addressMapper)
                .toResponse(address);
    }

    @Test
    void addAddress_shouldThrowException_whenUserNotFound() {

        mockAuthenticatedUser();

        when(userRepository.findById(USER_ID))
                .thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> addressService.addAddress(createRequest)
        );

        verify(userRepository)
                .findById(USER_ID);

        verifyNoInteractions(addressMapper);
        verifyNoInteractions(addressRepository);
    }

    // ============================================================
    // GET ADDRESS BY ID
    // ============================================================

    @Test
    void getAddressById_shouldReturnAddressSuccessfully() {

        mockAuthenticatedUser();

        when(addressRepository.findByIdAndUserId(
                ADDRESS_ID,
                USER_ID
        )).thenReturn(Optional.of(address));

        when(addressMapper.toResponse(address))
                .thenReturn(addressResponse);

        AddressResponse result =
                addressService.getAddressById(ADDRESS_ID);

        assertNotNull(result);
        assertSame(addressResponse, result);

        verify(addressRepository)
                .findByIdAndUserId(ADDRESS_ID, USER_ID);

        verify(addressMapper)
                .toResponse(address);
    }

    @Test
    void getAddressById_shouldThrowException_whenAddressNotFound() {

        mockAuthenticatedUser();

        when(addressRepository.findByIdAndUserId(
                ADDRESS_ID,
                USER_ID
        )).thenReturn(Optional.empty());

        assertThrows(
                AddressNotFoundException.class,
                () -> addressService.getAddressById(ADDRESS_ID)
        );

        verify(addressRepository)
                .findByIdAndUserId(ADDRESS_ID, USER_ID);

        verifyNoInteractions(addressMapper);
    }

    // ============================================================
    // GET USER ADDRESSES
    // ============================================================

    @Test
    void getUserAddresses_shouldReturnAllAddresses() {

        mockAuthenticatedUser();

        Address secondAddress = new Address();
        secondAddress.setId(30L);
        secondAddress.setUser(user);
        secondAddress.setDefaultAddress(false);

        AddressResponse secondResponse =
                new AddressResponse();

        when(addressRepository.findAllByUserId(USER_ID))
                .thenReturn(List.of(address, secondAddress));

        when(addressMapper.toResponse(address))
                .thenReturn(addressResponse);

        when(addressMapper.toResponse(secondAddress))
                .thenReturn(secondResponse);

        List<AddressResponse> result =
                addressService.getUserAddresses();

        assertNotNull(result);
        assertEquals(2, result.size());

        assertSame(addressResponse, result.get(0));
        assertSame(secondResponse, result.get(1));

        verify(addressRepository)
                .findAllByUserId(USER_ID);

        verify(addressMapper)
                .toResponse(address);

        verify(addressMapper)
                .toResponse(secondAddress);
    }

    @Test
    void getUserAddresses_shouldReturnEmptyList_whenNoAddressesExist() {

        mockAuthenticatedUser();

        when(addressRepository.findAllByUserId(USER_ID))
                .thenReturn(List.of());

        List<AddressResponse> result =
                addressService.getUserAddresses();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(addressRepository)
                .findAllByUserId(USER_ID);

        verifyNoInteractions(addressMapper);
    }

    // ============================================================
    // UPDATE ADDRESS
    // ============================================================

    @Test
    void updateAddress_shouldUpdateSuccessfully() {

        mockAuthenticatedUser();

        address.setDefaultAddress(false);

        when(addressRepository.findByIdAndUserId(
                ADDRESS_ID,
                USER_ID
        )).thenReturn(Optional.of(address));

        when(addressRepository.save(address))
                .thenReturn(address);

        when(addressMapper.toResponse(address))
                .thenReturn(addressResponse);

        AddressResponse result =
                addressService.updateAddress(
                        ADDRESS_ID,
                        updateRequest
                );

        assertNotNull(result);
        assertSame(addressResponse, result);

        verify(addressRepository)
                .findByIdAndUserId(ADDRESS_ID, USER_ID);

        verify(addressMapper)
                .updateEntity(address, updateRequest);

        verify(addressRepository)
                .save(address);

        verify(addressMapper)
                .toResponse(address);

        verify(
                addressRepository,
                never()
        ).findByUserIdAndDefaultAddressTrue(USER_ID);
    }

    @Test
    void updateAddress_shouldRemoveExistingDefaultAddress() {

        mockAuthenticatedUser();

        address.setDefaultAddress(true);

        when(addressRepository.findByIdAndUserId(
                ADDRESS_ID,
                USER_ID
        )).thenReturn(Optional.of(address));

        when(addressRepository.findByUserIdAndDefaultAddressTrue(
                USER_ID
        )).thenReturn(Optional.of(existingDefaultAddress));

        when(addressRepository.save(existingDefaultAddress))
                .thenReturn(existingDefaultAddress);

        when(addressRepository.save(address))
                .thenReturn(address);

        when(addressMapper.toResponse(address))
                .thenReturn(addressResponse);

        AddressResponse result =
                addressService.updateAddress(
                        ADDRESS_ID,
                        updateRequest
                );

        assertNotNull(result);
        assertSame(addressResponse, result);

        verify(addressMapper)
                .updateEntity(address, updateRequest);

        verify(addressRepository)
                .findByUserIdAndDefaultAddressTrue(USER_ID);

        verify(addressRepository)
                .save(existingDefaultAddress);

        verify(addressRepository)
                .save(address);

        verify(addressMapper)
                .toResponse(address);
    }

    @Test
    void updateAddress_shouldNotDisableSameAddress() {

        mockAuthenticatedUser();

        address.setDefaultAddress(true);

        when(addressRepository.findByIdAndUserId(
                ADDRESS_ID,
                USER_ID
        )).thenReturn(Optional.of(address));

        when(addressRepository.findByUserIdAndDefaultAddressTrue(
                USER_ID
        )).thenReturn(Optional.of(address));

        when(addressRepository.save(address))
                .thenReturn(address);

        when(addressMapper.toResponse(address))
                .thenReturn(addressResponse);

        AddressResponse result =
                addressService.updateAddress(
                        ADDRESS_ID,
                        updateRequest
                );

        assertNotNull(result);

        assertTrue(
                address.getDefaultAddress()
        );

        verify(addressRepository)
                .findByUserIdAndDefaultAddressTrue(USER_ID);

        verify(addressRepository)
                .save(address);

        verify(addressMapper)
                .toResponse(address);
    }

    @Test
    void updateAddress_shouldThrowException_whenAddressNotFound() {

        mockAuthenticatedUser();

        when(addressRepository.findByIdAndUserId(
                ADDRESS_ID,
                USER_ID
        )).thenReturn(Optional.empty());

        assertThrows(
                AddressNotFoundException.class,
                () -> addressService.updateAddress(
                        ADDRESS_ID,
                        updateRequest
                )
        );

        verify(addressRepository)
                .findByIdAndUserId(ADDRESS_ID, USER_ID);

        verifyNoInteractions(addressMapper);
    }

    // ============================================================
    // DELETE ADDRESS
    // ============================================================

    @Test
    void deleteAddress_shouldDeleteSuccessfully() {

        mockAuthenticatedUser();

        when(addressRepository.findByIdAndUserId(
                ADDRESS_ID,
                USER_ID
        )).thenReturn(Optional.of(address));

        addressService.deleteAddress(ADDRESS_ID);

        verify(addressRepository)
                .findByIdAndUserId(ADDRESS_ID, USER_ID);

        verify(addressRepository)
                .delete(address);
    }

    @Test
    void deleteAddress_shouldThrowException_whenAddressNotFound() {

        mockAuthenticatedUser();

        when(addressRepository.findByIdAndUserId(
                ADDRESS_ID,
                USER_ID
        )).thenReturn(Optional.empty());

        assertThrows(
                AddressNotFoundException.class,
                () -> addressService.deleteAddress(ADDRESS_ID)
        );

        verify(addressRepository)
                .findByIdAndUserId(ADDRESS_ID, USER_ID);

        verify(addressRepository, never())
                .delete(any(Address.class));
    }

    // ============================================================
    // SET DEFAULT ADDRESS
    // ============================================================

    @Test
    void setDefaultAddress_shouldSetAddressAsDefaultSuccessfully() {

        mockAuthenticatedUser();

        address.setDefaultAddress(false);

        when(addressRepository.findByIdAndUserId(
                ADDRESS_ID,
                USER_ID
        )).thenReturn(Optional.of(address));

        when(addressRepository.findByUserIdAndDefaultAddressTrue(
                USER_ID
        )).thenReturn(Optional.empty());

        when(addressRepository.save(address))
                .thenReturn(address);

        addressService.setDefaultAddress(ADDRESS_ID);

        assertTrue(
                address.getDefaultAddress()
        );

        verify(addressRepository)
                .findByIdAndUserId(ADDRESS_ID, USER_ID);

        verify(addressRepository)
                .findByUserIdAndDefaultAddressTrue(USER_ID);

        verify(addressRepository)
                .save(address);
    }

    @Test
    void setDefaultAddress_shouldRemoveOldDefaultAndSetNewDefault() {

        mockAuthenticatedUser();

        address.setDefaultAddress(false);

        when(addressRepository.findByIdAndUserId(
                ADDRESS_ID,
                USER_ID
        )).thenReturn(Optional.of(address));

        when(addressRepository.findByUserIdAndDefaultAddressTrue(
                USER_ID
        )).thenReturn(Optional.of(existingDefaultAddress));

        when(addressRepository.save(existingDefaultAddress))
                .thenReturn(existingDefaultAddress);

        when(addressRepository.save(address))
                .thenReturn(address);

        addressService.setDefaultAddress(ADDRESS_ID);

        assertFalse(
                existingDefaultAddress.getDefaultAddress()
        );

        assertTrue(
                address.getDefaultAddress()
        );

        verify(addressRepository)
                .findByUserIdAndDefaultAddressTrue(USER_ID);

        verify(addressRepository)
                .save(existingDefaultAddress);

        verify(addressRepository)
                .save(address);
    }

    @Test
    void setDefaultAddress_shouldKeepSameAddressDefault_whenAlreadyDefault() {

        mockAuthenticatedUser();

        address.setDefaultAddress(true);

        when(addressRepository.findByIdAndUserId(
                ADDRESS_ID,
                USER_ID
        )).thenReturn(Optional.of(address));

        when(addressRepository.findByUserIdAndDefaultAddressTrue(
                USER_ID
        )).thenReturn(Optional.of(address));

        when(addressRepository.save(address))
                .thenReturn(address);

        addressService.setDefaultAddress(ADDRESS_ID);

        assertTrue(
                address.getDefaultAddress()
        );

        verify(addressRepository)
                .findByUserIdAndDefaultAddressTrue(USER_ID);

        verify(addressRepository)
                .save(address);
    }

    @Test
    void setDefaultAddress_shouldThrowException_whenAddressNotFound() {

        mockAuthenticatedUser();

        when(addressRepository.findByIdAndUserId(
                ADDRESS_ID,
                USER_ID
        )).thenReturn(Optional.empty());

        assertThrows(
                AddressNotFoundException.class,
                () -> addressService.setDefaultAddress(ADDRESS_ID)
        );

        verify(addressRepository)
                .findByIdAndUserId(ADDRESS_ID, USER_ID);

        verify(
                addressRepository,
                never()
        ).findByUserIdAndDefaultAddressTrue(USER_ID);

        verify(
                addressRepository,
                never()
        ).save(any(Address.class));
    }

    // ============================================================
    // AUTHENTICATION FAILURE
    // ============================================================

    @Test
    void getUserAddresses_shouldThrowException_whenUserNotAuthenticated() {

        SecurityContextHolder.clearContext();

        assertThrows(
                UserNotFoundException.class,
                () -> addressService.getUserAddresses()
        );

        verifyNoInteractions(addressRepository);
        verifyNoInteractions(addressMapper);
        verifyNoInteractions(userRepository);
    }

    @Test
    void getAddressById_shouldThrowException_whenUserNotAuthenticated() {

        SecurityContextHolder.clearContext();

        assertThrows(
                UserNotFoundException.class,
                () -> addressService.getAddressById(ADDRESS_ID)
        );

        verifyNoInteractions(addressRepository);
        verifyNoInteractions(addressMapper);
    }
}

