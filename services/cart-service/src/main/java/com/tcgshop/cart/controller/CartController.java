package com.tcgshop.cart.controller;

import com.tcgshop.cart.model.CartItem;
import com.tcgshop.cart.model.ShoppingCart;
import com.tcgshop.cart.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for Cart operations
 */
@RestController
@RequestMapping("/cart")
@CrossOrigin(origins = "*")
public class CartController {

    private final CartService cartService;

    @Autowired
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping("/{sessionId}")
    public ResponseEntity<ShoppingCart> getCart(@PathVariable String sessionId) {
        ShoppingCart cart = cartService.getCart(sessionId);
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/{sessionId}/items")
    public ResponseEntity<ShoppingCart> addItem(
            @PathVariable String sessionId,
            @RequestBody CartItem item) {
        ShoppingCart cart = cartService.addItem(sessionId, item);
        return ResponseEntity.ok(cart);
    }

    @DeleteMapping("/{sessionId}/items/{productId}")
    public ResponseEntity<ShoppingCart> removeItem(
            @PathVariable String sessionId,
            @PathVariable Long productId) {
        ShoppingCart cart = cartService.removeItem(sessionId, productId);
        return ResponseEntity.ok(cart);
    }

    @PutMapping("/{sessionId}/items/{productId}")
    public ResponseEntity<ShoppingCart> updateItemQuantity(
            @PathVariable String sessionId,
            @PathVariable Long productId,
            @RequestParam Integer quantity) {
        ShoppingCart cart = cartService.updateItemQuantity(sessionId, productId, quantity);
        return ResponseEntity.ok(cart);
    }

    @DeleteMapping("/{sessionId}")
    public ResponseEntity<Void> clearCart(@PathVariable String sessionId) {
        cartService.clearCart(sessionId);
        return ResponseEntity.noContent().build();
    }
}

