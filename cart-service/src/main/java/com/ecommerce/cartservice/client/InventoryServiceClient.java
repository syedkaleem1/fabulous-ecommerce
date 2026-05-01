package com.ecommerce.cartservice.client;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class InventoryServiceClient {

    private final RestClient inventoryServiceRestClient;

    public boolean isProductInStock(Long productId, int quantity) {
        // Assuming inventory service has an endpoint like: GET /api/inventory/{productId}?quantity={quantity}
        // Returns true if sufficient stock
        Boolean inStock = inventoryServiceRestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/inventory/{productId}")
                        .queryParam("quantity", quantity)
                        .build(productId))
                .retrieve()
                .body(Boolean.class);
        return Boolean.TRUE.equals(inStock);
    }
}
