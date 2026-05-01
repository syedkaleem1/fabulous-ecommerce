//package com.ecommerce.cartservice.client;
//
//import org.springframework.cloud.openfeign.FeignClient;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//
//import java.math.BigDecimal;
//
//@FeignClient(name = "product-service", url = "${product.service.url}")
//public interface ProductServiceClient {
//
//    @GetMapping("/api/products/{id}")
//    ProductDto getProductById(@PathVariable("id") Long id);
//}
//
//// Simple DTO for product info
//class ProductDto {
//    private Long id;
//    private String name;
//    private BigDecimal price;
//    private String imageUrl;
//    // getters/setters
//}