package com.fabulous.order.dto.request;

import java.util.List;

// Sent to: POST http://inventory-service:8082/api/inventory/reserve
public record ReserveItemsRequest(
        String orderId,
        List<LineItem> items
) {
    public record LineItem(Long productId, int quantity) {}
}
