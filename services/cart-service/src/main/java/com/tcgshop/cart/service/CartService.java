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
        try {
            Object value = redisTemplate.opsForValue().get(key);
            if (value == null) {
                ShoppingCart cart = new ShoppingCart(sessionId);
                saveCart(cart);
                return cart;
            }
            // Handle deserialization - GenericJackson2JsonRedisSerializer may return LinkedHashMap
            if (value instanceof ShoppingCart) {
                ShoppingCart cart = (ShoppingCart) value;
                // Ensure items list is initialized
                if (cart.getItems() == null) {
                    cart = new ShoppingCart(sessionId);
                    saveCart(cart);
                }
                return cart;
            } else if (value instanceof java.util.Map) {
                // Deserialized as Map, convert to ShoppingCart
                System.out.println("INFO: Converting Map to ShoppingCart for sessionId=" + sessionId);
                try {
                    ShoppingCart cart = convertMapToShoppingCart((java.util.Map<?, ?>) value, sessionId);
                    System.out.println("INFO: Converted cart has " + cart.getItems().size() + " items, total=" + cart.getTotal());
                    // Save the properly typed cart back to Redis
                    saveCart(cart);
                    return cart;
                } catch (Exception e) {
                    System.out.println("ERROR converting Map to ShoppingCart: " + e.getMessage());
                    e.printStackTrace();
                    redisTemplate.delete(key);
                    ShoppingCart cart = new ShoppingCart(sessionId);
                    saveCart(cart);
                    return cart;
                }
            } else {
                // Unknown type, clear and create new cart
                System.out.println("WARNING: Retrieved value is not ShoppingCart instance: " + value.getClass().getName());
                redisTemplate.delete(key);
                ShoppingCart cart = new ShoppingCart(sessionId);
                saveCart(cart);
                return cart;
            }
        } catch (Exception e) {
            // Handle deserialization errors by clearing old data
            System.out.println("ERROR retrieving cart: " + e.getMessage());
            e.printStackTrace();
            redisTemplate.delete(key);
            ShoppingCart cart = new ShoppingCart(sessionId);
            saveCart(cart);
            return cart;
        }
    }
    
    @SuppressWarnings("unchecked")
    private ShoppingCart convertMapToShoppingCart(java.util.Map<?, ?> map, String sessionId) {
        ShoppingCart cart = new ShoppingCart(sessionId);
        
        // Convert items
        Object itemsObj = map.get("items");
        if (itemsObj instanceof java.util.List) {
            java.util.List<?> itemsList = (java.util.List<?>) itemsObj;
            for (Object itemObj : itemsList) {
                if (itemObj instanceof java.util.Map) {
                    java.util.Map<String, Object> itemMap = (java.util.Map<String, Object>) itemObj;
                    CartItem item = new CartItem();
                    if (itemMap.get("productId") instanceof Number) {
                        item.setProductId(((Number) itemMap.get("productId")).longValue());
                    }
                    if (itemMap.get("productName") != null) {
                        item.setProductName(itemMap.get("productName").toString());
                    }
                    if (itemMap.get("quantity") instanceof Number) {
                        item.setQuantity(((Number) itemMap.get("quantity")).intValue());
                    }
                    if (itemMap.get("price") != null) {
                        if (itemMap.get("price") instanceof Number) {
                            item.setPrice(java.math.BigDecimal.valueOf(((Number) itemMap.get("price")).doubleValue()));
                        } else if (itemMap.get("price") instanceof String) {
                            item.setPrice(new java.math.BigDecimal((String) itemMap.get("price")));
                        }
                    }
                    cart.getItems().add(item);
                }
            }
        }
        
        // Set total
        Object totalObj = map.get("total");
        if (totalObj != null) {
            if (totalObj instanceof Number) {
                cart.setTotal(java.math.BigDecimal.valueOf(((Number) totalObj).doubleValue()));
            } else if (totalObj instanceof String) {
                cart.setTotal(new java.math.BigDecimal((String) totalObj));
            }
        }
        
        cart.calculateTotal();
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
        try {
            redisTemplate.opsForValue().set(key, cart, CART_TTL);
            System.out.println("Cart saved: sessionId=" + cart.getSessionId() + ", items=" + (cart.getItems() != null ? cart.getItems().size() : 0) + ", total=" + cart.getTotal());
        } catch (Exception e) {
            System.out.println("ERROR saving cart: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
}

