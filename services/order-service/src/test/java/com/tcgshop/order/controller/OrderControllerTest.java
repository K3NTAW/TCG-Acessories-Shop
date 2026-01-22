package com.tcgshop.order.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tcgshop.order.dto.CreateOrderRequest;
import com.tcgshop.order.dto.OrderDTO;
import com.tcgshop.order.dto.OrderItemRequest;
import com.tcgshop.order.model.OrderStatus;
import com.tcgshop.order.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrderService orderService;

    private CreateOrderRequest createOrderRequest;
    private OrderDTO testOrderDTO;

    @BeforeEach
    void setUp() {
        createOrderRequest = new CreateOrderRequest();
        createOrderRequest.setCustomerId(1L);
        createOrderRequest.setShippingAddressId(1L);
        
        OrderItemRequest itemRequest = new OrderItemRequest();
        itemRequest.setProductId(1L);
        itemRequest.setQuantity(2);
        itemRequest.setUnitPrice(new BigDecimal("29.99"));
        createOrderRequest.setItems(Arrays.asList(itemRequest));

        testOrderDTO = new OrderDTO();
        testOrderDTO.setId(1L);
        testOrderDTO.setOrderNumber("ORD-123");
        testOrderDTO.setCustomerId(1L);
        testOrderDTO.setStatus(OrderStatus.PENDING);
        testOrderDTO.setTotalAmount(new BigDecimal("59.98"));
        testOrderDTO.setShippingAddressId(1L);
        testOrderDTO.setCreatedAt(LocalDateTime.now());
        testOrderDTO.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void testCreateOrder() throws Exception {
        // Given
        when(orderService.createOrder(any(CreateOrderRequest.class))).thenReturn(testOrderDTO);

        // When & Then
        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createOrderRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.orderNumber").value("ORD-123"))
                .andExpect(jsonPath("$.status").value("PENDING"));

        verify(orderService).createOrder(any(CreateOrderRequest.class));
    }

    @Test
    void testCreateOrder_ValidationError() throws Exception {
        // Given: Invalid request (missing customerId)
        CreateOrderRequest invalidRequest = new CreateOrderRequest();
        // Missing customerId and items

        // When & Then
        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(orderService, never()).createOrder(any());
    }

    @Test
    void testGetOrderById() throws Exception {
        // Given
        when(orderService.getOrderById(1L)).thenReturn(testOrderDTO);

        // When & Then
        mockMvc.perform(get("/orders/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.orderNumber").value("ORD-123"));

        verify(orderService).getOrderById(1L);
    }

    @Test
    void testGetOrdersByCustomer() throws Exception {
        // Given
        List<OrderDTO> orders = Arrays.asList(testOrderDTO);
        when(orderService.getOrdersByCustomerId(1L)).thenReturn(orders);

        // When & Then
        mockMvc.perform(get("/orders/customer/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].customerId").value(1));

        verify(orderService).getOrdersByCustomerId(1L);
    }

    @Test
    void testProcessPayment() throws Exception {
        // Given
        when(orderService.processPayment(1L)).thenReturn(testOrderDTO);

        // When & Then
        mockMvc.perform(post("/orders/1/payment"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1));

        verify(orderService).processPayment(1L);
    }

    @Test
    void testUpdateOrderStatus() throws Exception {
        // Given
        testOrderDTO.setStatus(OrderStatus.PROCESSING);
        when(orderService.updateOrderStatus(1L, OrderStatus.PROCESSING)).thenReturn(testOrderDTO);

        // When & Then
        mockMvc.perform(put("/orders/1/status")
                        .param("status", "PROCESSING"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("PROCESSING"));

        verify(orderService).updateOrderStatus(1L, OrderStatus.PROCESSING);
    }
}

