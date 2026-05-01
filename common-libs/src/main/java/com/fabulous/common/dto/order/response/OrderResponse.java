package com.fabulous.common.dto.order.response;


import com.fabulous.order.model.Order;
import com.fabulous.order.model.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrderResponse(
        Long orderId,
        String orderNumber,
        OrderStatus status,
        BigDecimal totalAmount,
        List<OrderItemResponse> items,
        String paymentIntentId,
        Instant createdAt,
        String message
) {
    public record OrderItemResponse(
            Long productId,
            String productName,
            int quantity,
            BigDecimal unitPrice,
            BigDecimal subtotal
    ) {}

    public static OrderResponse from(Order order, String message) {
        List<OrderItemResponse> itemResponses = order.getItems().stream()
                .map(i -> new OrderItemResponse(
                        i.getProductId(),
                        i.getProductName(),
                        i.getQuantity(),
                        i.getUnitPrice(),
                        i.getSubtotal()))
                .toList();

        return new OrderResponse(
                order.getId(),
                order.getOrderNumber(),
                order.getStatus(),
                order.getTotalAmount(),
                itemResponses,
                order.getPaymentIntentId(),
                order.getCreatedAt(),
                message
        );
    }
}
