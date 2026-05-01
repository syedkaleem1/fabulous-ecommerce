package com.fabulous.inventory.exception;

public class InventoryNotFoundException extends RuntimeException {
    public InventoryNotFoundException(String message) {
        super(message);
    }
    public InventoryNotFoundException(Long productId, Long warehouseId) {
        super("Inventory not found for productId=" + productId + ", warehouseId=" + warehouseId);
    }
}
