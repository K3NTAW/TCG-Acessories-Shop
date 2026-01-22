package com.tcgshop.cart.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tcgshop.cart.model.CartItem;
import com.tcgshop.cart.model.ShoppingCart;
import com.tcgshop.cart.service.CartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CartController.class)
class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CartService cartService;

    private String sessionId;
    private ShoppingCart testCart;
    private CartItem testItem;

    @BeforeEach
    void setUp() {
        sessionId = "session-123";
        testCart = new ShoppingCart(sessionId);
        testItem = new CartItem(1L, "Test Product", 2, new BigDecimal("29.99"));
        testCart.getItems().add(testItem);
        testCart.calculateTotal();
    }

    @Test
    void testGetCart() throws Exception {
        // Given
        when(cartService.getCart(sessionId)).thenReturn(testCart);

        // When & Then
        mockMvc.perform(get("/cart/{sessionId}", sessionId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.sessionId").value(sessionId))
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items[0].productId").value(1));

        verify(cartService).getCart(sessionId);
    }

    @Test
    void testAddItem() throws Exception {
        // Given
        when(cartService.addItem(eq(sessionId), any(CartItem.class))).thenReturn(testCart);

        // When & Then
        mockMvc.perform(post("/cart/{sessionId}/items", sessionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testItem)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.sessionId").value(sessionId));

        verify(cartService).addItem(eq(sessionId), any(CartItem.class));
    }

    @Test
    void testRemoveItem() throws Exception {
        // Given
        ShoppingCart updatedCart = new ShoppingCart(sessionId);
        when(cartService.removeItem(sessionId, 1L)).thenReturn(updatedCart);

        // When & Then
        mockMvc.perform(delete("/cart/{sessionId}/items/{productId}", sessionId, 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(cartService).removeItem(sessionId, 1L);
    }

    @Test
    void testUpdateItemQuantity() throws Exception {
        // Given
        ShoppingCart updatedCart = new ShoppingCart(sessionId);
        updatedCart.getItems().add(new CartItem(1L, "Test Product", 5, new BigDecimal("29.99")));
        updatedCart.calculateTotal();
        when(cartService.updateItemQuantity(sessionId, 1L, 5)).thenReturn(updatedCart);

        // When & Then
        mockMvc.perform(put("/cart/{sessionId}/items/{productId}", sessionId, 1L)
                        .param("quantity", "5"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(cartService).updateItemQuantity(sessionId, 1L, 5);
    }

    @Test
    void testClearCart() throws Exception {
        // Given
        doNothing().when(cartService).clearCart(sessionId);

        // When & Then
        mockMvc.perform(delete("/cart/{sessionId}", sessionId))
                .andExpect(status().isNoContent());

        verify(cartService).clearCart(sessionId);
    }
}

