package com.fabulous.inventory.dto;

import java.util.List;

public record ReserveRequest(String orderId, List<LineItem> items) {
    public record LineItem(Long productId, int quantity) {}
}
