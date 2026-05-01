package com.ecommerce.cartservice.client;

import com.ecommerce.cartservice.dto.request.ProductDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class ProductServiceClient {

    private final RestClient productServiceRestClient;  // injected from config

    public ProductDto getProductById(Long productId) {
        return productServiceRestClient.get()
                .uri("/api/products/{id}", productId)
                .retrieve()
                .onStatus(status -> status.is4xxClientError(), (request, response) -> {
                    throw new RuntimeException("Product service error: " + response.getStatusText());
                })
                .body(ProductDto.class);
    }
}
