package com.tcgshop.customer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tcgshop.customer.dto.LoginRequest;
import com.tcgshop.customer.dto.RegisterRequest;
import com.tcgshop.customer.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;

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
    }

    @Test
    void testRegister_Success() throws Exception {
        // Given
        String token = "test-jwt-token";
        when(authService.register(any(RegisterRequest.class))).thenReturn(token);

        // When & Then
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token").value(token))
                .andExpect(jsonPath("$.message").value("Registration successful"));

        verify(authService).register(any(RegisterRequest.class));
    }

    @Test
    void testRegister_ValidationError() throws Exception {
        // Given: Invalid request (missing email)
        RegisterRequest invalidRequest = new RegisterRequest();
        invalidRequest.setPassword("password123");

        // When & Then
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(authService, never()).register(any());
    }

    @Test
    void testLogin_Success() throws Exception {
        // Given
        String token = "test-jwt-token";
        when(authService.login(any(LoginRequest.class))).thenReturn(token);

        // When & Then
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token").value(token))
                .andExpect(jsonPath("$.message").value("Login successful"));

        verify(authService).login(any(LoginRequest.class));
    }

    @Test
    void testLogin_ValidationError() throws Exception {
        // Given: Invalid request (missing email)
        LoginRequest invalidRequest = new LoginRequest();
        invalidRequest.setPassword("password123");

        // When & Then
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(authService, never()).login(any());
    }
}

