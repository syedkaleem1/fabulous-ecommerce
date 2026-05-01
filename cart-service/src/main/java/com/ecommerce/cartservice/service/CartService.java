package com.ecommerce.cartservice.service;


import com.ecommerce.cartservice.client.ProductServiceClient;
import com.ecommerce.cartservice.dto.request.AddToCartRequest;
import com.ecommerce.cartservice.dto.request.ProductDto;
import com.ecommerce.cartservice.dto.response.CartItemResponse;
import com.ecommerce.cartservice.dto.response.CartResponse;
import com.ecommerce.cartservice.entity.Cart;
import com.ecommerce.cartservice.entity.CartItem;
import com.ecommerce.cartservice.enums.CartStatus;
import com.ecommerce.cartservice.repository.CartItemRepository;
import com.ecommerce.cartservice.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductServiceClient productServiceClient;
    // Optional: private final InventoryServiceClient inventoryServiceClient;

    // Get or create active cart for user
    private Cart getOrCreateCart(Long userId) {
        return cartRepository.findByUserIdAndStatus(userId, CartStatus.ACTIVE)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUserId(userId);
                    return cartRepository.save(newCart);
                });
    }

    @Transactional
    public CartResponse addItemToCart(Long userId, AddToCartRequest request) {
        // Optional: check stock before adding
        // if (!inventoryServiceClient.isProductInStock(request.getProductId(), request.getQuantity())) {
        //     throw new RuntimeException("Insufficient stock");
        // }

        Cart cart = getOrCreateCart(userId);

        // Fetch product details from Product Service
        ProductDto product = productServiceClient.getProductById(request.getProductId());

        // Check if item already exists in cart
        CartItem existingItem = cart.getItems().stream()
                .filter(item -> item.getProductId().equals(request.getProductId()))
                .findFirst()
                .orElse(null);

        if (existingItem != null) {
            // Update quantity
            existingItem.setQuantity(existingItem.getQuantity() + request.getQuantity());
        } else {
            // Create new cart item
            CartItem newItem = new CartItem();
            newItem.setProductId(request.getProductId());
            newItem.setQuantity(request.getQuantity());
            newItem.setPrice(product.getPrice()); // snapshot
            newItem.setCart(cart);
            cart.getItems().add(newItem);
        }

        cartRepository.save(cart);
        return mapToCartResponse(cart);
    }

    @Transactional(readOnly = true)
    public CartResponse getCart(Long userId) {
        Cart cart = cartRepository.findByUserIdAndStatus(userId, CartStatus.ACTIVE)
                .orElse(new Cart()); // empty cart if none
        return mapToCartResponse(cart);
    }

    @Transactional
    public CartResponse updateItemQuantity(Long userId, Long itemId, int quantity) {
        Cart cart = getActiveCart(userId);
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found"));
        if (!item.getCart().getId().equals(cart.getId())) {
            throw new RuntimeException("Item does not belong to user's cart");
        }
        if (quantity <= 0) {
            cartItemRepository.delete(item);
        } else {
            item.setQuantity(quantity);
            cartItemRepository.save(item);
        }
        return mapToCartResponse(cart);
    }

    @Transactional
    public CartResponse removeItem(Long userId, Long itemId) {
        Cart cart = getActiveCart(userId);
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found"));
        if (!item.getCart().getId().equals(cart.getId())) {
            throw new RuntimeException("Item does not belong to user's cart");
        }
        cartItemRepository.delete(item);
        return mapToCartResponse(cart);
    }

    @Transactional
    public void clearCart(Long userId) {
        Cart cart = getActiveCart(userId);
        cartItemRepository.deleteAll(cart.getItems());
        cart.getItems().clear();
        cartRepository.save(cart);
    }

    // Helper: get active cart or throw if not found
    private Cart getActiveCart(Long userId) {
        return cartRepository.findByUserIdAndStatus(userId, CartStatus.ACTIVE)
                .orElseThrow(() -> new RuntimeException("Active cart not found"));
    }

    // Mapping logic
    private CartResponse mapToCartResponse(Cart cart) {
        CartResponse response = new CartResponse();
        response.setCartId(cart.getId());
        response.setUserId(cart.getUserId());

        List<CartItemResponse> itemResponses = cart.getItems().stream()
                .map(item -> {
                    // Fetch product name from product service (could cache)
                    ProductDto product = productServiceClient.getProductById(item.getProductId());
                    BigDecimal totalPrice = item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
                    return new CartItemResponse(
                            item.getId(),
                            item.getProductId(),
                            product.getName(),
                            product.getImageUrl(),
                            item.getQuantity(),
                            item.getPrice(),
                            totalPrice
                    );
                })
                .collect(Collectors.toList());

        response.setItems(itemResponses);
        response.setTotalItems(itemResponses.size());
        response.setTotalAmount(itemResponses.stream()
                .map(CartItemResponse::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        return response;
    }
}