package com.fabulous.common.dto.order.response;

// Received from Inventory Service
public record InventoryResponse(
        boolean success,
        String message,
        String failedProductId
) {}