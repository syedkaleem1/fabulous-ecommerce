package com.fabulous.inventory.exception;

public class DuplicateInventoryException extends RuntimeException {
    public DuplicateInventoryException(Long productId, Long warehouseId) {
        super("Inventory already exists for productId=" + productId + ", warehouseId=" + warehouseId);
    }
}