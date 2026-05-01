package com.fabulous.order.dto.request;

import java.util.List;

// Sent to: POST http://inventory-service:8082/api/inventory/release
public record ReleaseItemsRequest(
        String orderId,
        List<LineItem> items
) {
    public record LineItem(Long productId, int quantity) {}
}