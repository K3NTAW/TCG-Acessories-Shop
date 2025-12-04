package com.tcgshop.cart.service;

import com.tcgshop.cart.model.CartItem;
import com.tcgshop.cart.model.ShoppingCart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

/**
 * Cart Service for managing shopping carts in Redis
 */
@Service
public class CartService {

    private static final String CART_KEY_PREFIX = "cart:";
    private static final Duration CART_TTL = Duration.ofDays(7);

    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public CartService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public ShoppingCart getCart(String sessionId) {
        String key = CART_KEY_PREFIX + sessionId;
        ShoppingCart cart = (ShoppingCart) redisTemplate.opsForValue().get(key);
        if (cart == null) {
            cart = new ShoppingCart(sessionId);
            saveCart(cart);
        }
        return cart;
    }

    public ShoppingCart addItem(String sessionId, CartItem item) {
        ShoppingCart cart = getCart(sessionId);
        
        // Check if item already exists
        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(i -> i.getProductId().equals(item.getProductId()))
                .findFirst();

        if (existingItem.isPresent()) {
            // Update quantity
            CartItem found = existingItem.get();
            found.setQuantity(found.getQuantity() + item.getQuantity());
        } else {
            // Add new item
            cart.getItems().add(item);
        }

        cart.calculateTotal();
        saveCart(cart);
        return cart;
    }

    public ShoppingCart removeItem(String sessionId, Long productId) {
        ShoppingCart cart = getCart(sessionId);
        cart.getItems().removeIf(item -> item.getProductId().equals(productId));
        cart.calculateTotal();
        saveCart(cart);
        return cart;
    }

    public ShoppingCart updateItemQuantity(String sessionId, Long productId, Integer quantity) {
        ShoppingCart cart = getCart(sessionId);
        cart.getItems().stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst()
                .ifPresent(item -> {
                    if (quantity <= 0) {
                        cart.getItems().remove(item);
                    } else {
                        item.setQuantity(quantity);
                    }
                });
        cart.calculateTotal();
        saveCart(cart);
        return cart;
    }

    public void clearCart(String sessionId) {
        String key = CART_KEY_PREFIX + sessionId;
        redisTemplate.delete(key);
    }

    private void saveCart(ShoppingCart cart) {
        String key = CART_KEY_PREFIX + cart.getSessionId();
        redisTemplate.opsForValue().set(key, cart, CART_TTL);
    }
}

