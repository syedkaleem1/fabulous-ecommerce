package com.fabulous.order.client;

import com.fabulous.order.dto.response.CartResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class CartServiceClient {

    private final RestClient cartServiceRestClient;

    public CartResponse getCart(Long userId) {
        return cartServiceRestClient.get()
                .uri("/api/cart/view-cart/{userId}", userId)
                .retrieve()
                .body(CartResponse.class);
    }

    public void clearCart(Long userId) {
        cartServiceRestClient.delete()
                .uri("/api/cart?userId={userId}", userId) // or with header
                .retrieve()
                .toBodilessEntity();
    }
}
