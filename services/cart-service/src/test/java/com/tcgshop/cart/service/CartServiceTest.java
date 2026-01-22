package com.tcgshop.cart.service;

import com.tcgshop.cart.model.CartItem;
import com.tcgshop.cart.model.ShoppingCart;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @InjectMocks
    private CartService cartService;

    private String sessionId;
    private CartItem testItem;

    @BeforeEach
    void setUp() {
        sessionId = "session-123";
        testItem = new CartItem(1L, "Test Product", 2, new BigDecimal("29.99"));
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void testGetCart_NewCart() {
        // Given
        when(valueOperations.get("cart:" + sessionId)).thenReturn(null);

        // When
        ShoppingCart cart = cartService.getCart(sessionId);

        // Then
        assertThat(cart).isNotNull();
        assertThat(cart.getSessionId()).isEqualTo(sessionId);
        assertThat(cart.getItems()).isEmpty();
        assertThat(cart.getTotal()).isEqualByComparingTo(BigDecimal.ZERO);
        verify(valueOperations).get("cart:" + sessionId);
        verify(redisTemplate).opsForValue().set(eq("cart:" + sessionId), any(ShoppingCart.class), any());
    }

    @Test
    void testGetCart_ExistingCart() {
        // Given
        ShoppingCart existingCart = new ShoppingCart(sessionId);
        existingCart.getItems().add(testItem);
        existingCart.calculateTotal();
        when(valueOperations.get("cart:" + sessionId)).thenReturn(existingCart);

        // When
        ShoppingCart cart = cartService.getCart(sessionId);

        // Then
        assertThat(cart).isNotNull();
        assertThat(cart.getItems()).hasSize(1);
        assertThat(cart.getTotal()).isGreaterThan(BigDecimal.ZERO);
        verify(valueOperations).get("cart:" + sessionId);
    }

    @Test
    void testAddItem_NewItem() {
        // Given
        ShoppingCart cart = new ShoppingCart(sessionId);
        when(valueOperations.get("cart:" + sessionId)).thenReturn(cart);

        // When
        ShoppingCart result = cartService.addItem(sessionId, testItem);

        // Then
        assertThat(result.getItems()).hasSize(1);
        assertThat(result.getItems().get(0).getProductId()).isEqualTo(1L);
        assertThat(result.getItems().get(0).getQuantity()).isEqualTo(2);
        verify(redisTemplate).opsForValue().set(eq("cart:" + sessionId), any(ShoppingCart.class), any());
    }

    @Test
    void testAddItem_ExistingItem() {
        // Given
        ShoppingCart cart = new ShoppingCart(sessionId);
        cart.getItems().add(testItem);
        cart.calculateTotal();
        when(valueOperations.get("cart:" + sessionId)).thenReturn(cart);

        CartItem additionalItem = new CartItem(1L, "Test Product", 1, new BigDecimal("29.99"));

        // When
        ShoppingCart result = cartService.addItem(sessionId, additionalItem);

        // Then
        assertThat(result.getItems()).hasSize(1);
        assertThat(result.getItems().get(0).getQuantity()).isEqualTo(3); // 2 + 1
        verify(redisTemplate).opsForValue().set(eq("cart:" + sessionId), any(ShoppingCart.class), any());
    }

    @Test
    void testRemoveItem() {
        // Given
        ShoppingCart cart = new ShoppingCart(sessionId);
        cart.getItems().add(testItem);
        cart.calculateTotal();
        when(valueOperations.get("cart:" + sessionId)).thenReturn(cart);

        // When
        ShoppingCart result = cartService.removeItem(sessionId, 1L);

        // Then
        assertThat(result.getItems()).isEmpty();
        verify(redisTemplate).opsForValue().set(eq("cart:" + sessionId), any(ShoppingCart.class), any());
    }

    @Test
    void testUpdateItemQuantity() {
        // Given
        ShoppingCart cart = new ShoppingCart(sessionId);
        cart.getItems().add(testItem);
        cart.calculateTotal();
        when(valueOperations.get("cart:" + sessionId)).thenReturn(cart);

        // When
        ShoppingCart result = cartService.updateItemQuantity(sessionId, 1L, 5);

        // Then
        assertThat(result.getItems()).hasSize(1);
        assertThat(result.getItems().get(0).getQuantity()).isEqualTo(5);
        verify(redisTemplate).opsForValue().set(eq("cart:" + sessionId), any(ShoppingCart.class), any());
    }

    @Test
    void testUpdateItemQuantity_RemoveWhenZero() {
        // Given
        ShoppingCart cart = new ShoppingCart(sessionId);
        cart.getItems().add(testItem);
        cart.calculateTotal();
        when(valueOperations.get("cart:" + sessionId)).thenReturn(cart);

        // When
        ShoppingCart result = cartService.updateItemQuantity(sessionId, 1L, 0);

        // Then
        assertThat(result.getItems()).isEmpty();
        verify(redisTemplate).opsForValue().set(eq("cart:" + sessionId), any(ShoppingCart.class), any());
    }

    @Test
    void testClearCart() {
        // Given
        doNothing().when(redisTemplate).delete("cart:" + sessionId);

        // When
        cartService.clearCart(sessionId);

        // Then
        verify(redisTemplate).delete("cart:" + sessionId);
    }
}

