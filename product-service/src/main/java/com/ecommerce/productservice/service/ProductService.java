package com.ecommerce.productservice.service;

import com.ecommerce.productservice.dto.ProductRequest;
import com.ecommerce.productservice.dto.ProductResponse;
import com.ecommerce.productservice.entity.Product;
import com.ecommerce.productservice.exception.ProductNotFoundException;
import com.ecommerce.productservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public ProductResponse addProduct(ProductRequest request) {
        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stock(request.getStock())
                .category(request.getCategory())
                .brand(request.getBrand())
                .imageUrl(request.getImageUrl())
                .active(request.getActive() != null ? request.getActive() : true)
                .build();
        return ProductResponse.fromEntity(productRepository.save(product));
    }

    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setCategory(request.getCategory());
        product.setBrand(request.getBrand());
        product.setImageUrl(request.getImageUrl());
        product.setActive(request.getActive() != null ? request.getActive() : product.getActive());

        return ProductResponse.fromEntity(productRepository.save(product));
    }

    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
        productRepository.delete(product);
    }

    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
        return ProductResponse.fromEntity(product);
    }

    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(ProductResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public List<ProductResponse> searchByCategory(String category) {
        return productRepository.findByCategoryContainingIgnoreCase(category)
                .stream()
                .map(ProductResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public List<ProductResponse> searchByName(String name) {
        return productRepository.findByNameContainingIgnoreCase(name)
                .stream()
                .map(ProductResponse::fromEntity)
                .collect(Collectors.toList());
    }
}

