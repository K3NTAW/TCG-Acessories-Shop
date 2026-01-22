package com.tcgshop.payment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tcgshop.payment.model.Payment;
import com.tcgshop.payment.model.PaymentStatus;
import com.tcgshop.payment.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PaymentController.class)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PaymentService paymentService;

    private Payment testPayment;
    private Map<String, Object> paymentRequest;

    @BeforeEach
    void setUp() {
        testPayment = new Payment();
        testPayment.setId(1L);
        testPayment.setOrderId(1L);
        testPayment.setOrderNumber("ORD-123");
        testPayment.setAmount(new BigDecimal("59.98"));
        testPayment.setStatus(PaymentStatus.COMPLETED);
        testPayment.setPaymentMethod("CREDIT_CARD");
        testPayment.setCreatedAt(LocalDateTime.now());

        paymentRequest = new HashMap<>();
        paymentRequest.put("orderId", 1L);
        paymentRequest.put("orderNumber", "ORD-123");
        paymentRequest.put("amount", 59.98);
        paymentRequest.put("customerId", 1L);
    }

    @Test
    void testProcessPayment() throws Exception {
        // Given
        when(paymentService.processPayment(any())).thenReturn(testPayment);

        // When & Then
        mockMvc.perform(post("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(paymentRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.orderId").value(1))
                .andExpect(jsonPath("$.status").value("COMPLETED"));

        verify(paymentService).processPayment(any());
    }

    @Test
    void testGetPayment() throws Exception {
        // Given
        when(paymentService.getPayment(1L)).thenReturn(testPayment);

        // When & Then
        mockMvc.perform(get("/payments/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.orderId").value(1));

        verify(paymentService).getPayment(1L);
    }

    @Test
    void testGetPayment_NotFound() throws Exception {
        // Given
        when(paymentService.getPayment(999L)).thenReturn(null);

        // When & Then
        mockMvc.perform(get("/payments/999"))
                .andExpect(status().isNotFound());

        verify(paymentService).getPayment(999L);
    }

    @Test
    void testGetPaymentByOrderId() throws Exception {
        // Given
        when(paymentService.getPaymentByOrderId(1L)).thenReturn(testPayment);

        // When & Then
        mockMvc.perform(get("/payments/order/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.orderId").value(1));

        verify(paymentService).getPaymentByOrderId(1L);
    }

    @Test
    void testGetPaymentByOrderId_NotFound() throws Exception {
        // Given
        when(paymentService.getPaymentByOrderId(999L)).thenReturn(null);

        // When & Then
        mockMvc.perform(get("/payments/order/999"))
                .andExpect(status().isNotFound());

        verify(paymentService).getPaymentByOrderId(999L);
    }
}

