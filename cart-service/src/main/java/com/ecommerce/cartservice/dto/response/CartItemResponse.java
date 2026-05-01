package com.ecommerce.cartservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemResponse {
    private Long itemId;
    private Long productId;
    private String productName;
    private String productImage;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
}