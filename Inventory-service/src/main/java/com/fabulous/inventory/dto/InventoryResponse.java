package com.fabulous.inventory.dto;

public record InventoryResponse(
        boolean success,
        String message,
        String failedProductId
) {
    public static InventoryResponse ok() {
        return new InventoryResponse(true, "OK", null);
    }
    public static InventoryResponse fail(String message, String failedProductId) {
        return new InventoryResponse(false, message, failedProductId);
    }
}

