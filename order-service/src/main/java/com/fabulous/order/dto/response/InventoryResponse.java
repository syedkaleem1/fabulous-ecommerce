package com.fabulous.order.dto.response;

// Received from Inventory Service
public record InventoryResponse(
        boolean success,
        String message,
        String failedProductId
) {}