package com.ecommerce.cartservice.controller;

import com.ecommerce.cartservice.dto.request.AddToCartRequest;
import com.ecommerce.cartservice.dto.response.CartResponse;
import com.ecommerce.cartservice.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    // Assume userId is extracted from JWT token; here we pass as header for simplicity
    @PostMapping("/items")
    public ResponseEntity<CartResponse> addToCart(@RequestHeader("X-User-Id") Long userId,
                                                  @Valid @RequestBody AddToCartRequest request) {
        return ResponseEntity.ok(cartService.addItemToCart(userId, request));
    }

    @GetMapping("/view")
    public ResponseEntity<CartResponse> viewCart(@RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(cartService.getCart(userId));
    }

    @GetMapping("/view-cart/{userId}")
    public ResponseEntity<CartResponse> viewCartItem(@PathVariable Long userId) {
        return ResponseEntity.ok(cartService.getCart(userId));
    }

    @PutMapping("/items/{itemId}")
    public ResponseEntity<CartResponse> updateQuantity(@RequestHeader("X-User-Id") Long userId,
                                                       @PathVariable Long itemId,
                                                       @RequestParam int quantity) {
        return ResponseEntity.ok(cartService.updateItemQuantity(userId, itemId, quantity));
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<CartResponse> removeItem(@RequestHeader("X-User-Id") Long userId,
                                                   @PathVariable Long itemId) {
        return ResponseEntity.ok(cartService.removeItem(userId, itemId));
    }

    @DeleteMapping
    public ResponseEntity<Void> clearCart(@RequestHeader("X-User-Id") Long userId) {
        cartService.clearCart(userId);
        return ResponseEntity.noContent().build();
    }
}
