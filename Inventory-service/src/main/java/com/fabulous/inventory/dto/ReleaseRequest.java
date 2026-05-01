package com.fabulous.inventory.dto;

import java.util.List;

public record ReleaseRequest(String orderId, List<LineItem> items) {
    public record LineItem(Long productId, int quantity) {}
}
