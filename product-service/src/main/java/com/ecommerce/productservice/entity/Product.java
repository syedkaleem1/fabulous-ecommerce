package com.ecommerce.productservice.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "PRODUCT_DETAILS")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "PRODUCT_NAME", nullable = false)
    private String name;

    @Column(name = "DESCRIPTION", length = 2000)
    private String description;

    @Column(name= "PRICE",nullable = false)
    private BigDecimal price;

    @Column(name = "STOCK", nullable = false)
    private Integer stock;

    @Column(name = "CATEGORY", nullable = false)
    private String category;

    @Column(name = "BRAND")
    private String brand;

    @Column(name = "IMAGE_URL")
    private String imageUrl;

    @Column(name = "ACTIVE")
    private Boolean active = true;

    @Column(name = "CREATED_DATE", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "UPDATED_DATE")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
