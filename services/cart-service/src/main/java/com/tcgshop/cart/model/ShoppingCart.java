package com.tcgshop.cart.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Shopping Cart Model
 */
public class ShoppingCart {
    private String sessionId;
    private List<CartItem> items;
    private BigDecimal total;
    private LocalDateTime updatedAt;

    public ShoppingCart() {
        this.items = new ArrayList<>();
        this.total = BigDecimal.ZERO;
        this.updatedAt = LocalDateTime.now();
    }

    public ShoppingCart(String sessionId) {
        this();
        this.sessionId = sessionId;
    }

    public void calculateTotal() {
        this.total = items.stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
        calculateTotal();
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}

