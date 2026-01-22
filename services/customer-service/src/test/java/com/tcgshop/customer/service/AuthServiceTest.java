package com.tcgshop.customer.service;

import com.tcgshop.customer.dto.LoginRequest;
import com.tcgshop.customer.dto.RegisterRequest;
import com.tcgshop.customer.model.Customer;
import com.tcgshop.customer.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private Customer testCustomer;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("password123");
        registerRequest.setFirstName("Test");
        registerRequest.setLastName("User");

        loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");

        testCustomer = new Customer();
        testCustomer.setId(1L);
        testCustomer.setEmail("test@example.com");
        testCustomer.setPasswordHash("$2a$10$encodedPasswordHash");
        testCustomer.setFirstName("Test");
        testCustomer.setLastName("User");
    }

    @Test
    void testRegister_Success() {
        // Given
        when(customerRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(customerRepository.save(any(Customer.class))).thenReturn(testCustomer);
        when(passwordEncoder.encode("password123")).thenReturn("$2a$10$encodedPasswordHash");

        // When
        String token = authService.register(registerRequest);

        // Then
        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
        verify(customerRepository).findByEmail("test@example.com");
        verify(customerRepository).save(any(Customer.class));
        verify(passwordEncoder).encode("password123");
    }

    @Test
    void testRegister_EmailAlreadyExists() {
        // Given
        when(customerRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testCustomer));

        // When & Then
        assertThatThrownBy(() -> authService.register(registerRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Email already exists");

        verify(customerRepository).findByEmail("test@example.com");
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void testLogin_Success() {
        // Given
        when(customerRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testCustomer));
        when(passwordEncoder.matches("password123", "$2a$10$encodedPasswordHash")).thenReturn(true);

        // When
        String token = authService.login(loginRequest);

        // Then
        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
        verify(customerRepository).findByEmail("test@example.com");
        verify(passwordEncoder).matches("password123", "$2a$10$encodedPasswordHash");
    }

    @Test
    void testLogin_InvalidEmail() {
        // Given
        when(customerRepository.findByEmail("wrong@example.com")).thenReturn(Optional.empty());

        LoginRequest invalidRequest = new LoginRequest();
        invalidRequest.setEmail("wrong@example.com");
        invalidRequest.setPassword("password123");

        // When & Then
        assertThatThrownBy(() -> authService.login(invalidRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Invalid credentials");

        verify(customerRepository).findByEmail("wrong@example.com");
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    void testLogin_InvalidPassword() {
        // Given
        when(customerRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testCustomer));
        when(passwordEncoder.matches("wrongpassword", "$2a$10$encodedPasswordHash")).thenReturn(false);

        LoginRequest invalidRequest = new LoginRequest();
        invalidRequest.setEmail("test@example.com");
        invalidRequest.setPassword("wrongpassword");

        // When & Then
        assertThatThrownBy(() -> authService.login(invalidRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Invalid credentials");

        verify(customerRepository).findByEmail("test@example.com");
        verify(passwordEncoder).matches("wrongpassword", "$2a$10$encodedPasswordHash");
    }
}

