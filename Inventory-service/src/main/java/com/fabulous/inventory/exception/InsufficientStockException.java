package com.fabulous.inventory.exception;

public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(String message) {
        super(message);
    }
    public InsufficientStockException(Long productId, Long warehouseId, int requested, int available) {
        super(String.format(
                "Insufficient stock for productId=%d, warehouseId=%d. Requested: %d, Available: %d",
                productId, warehouseId, requested, available
        ));
    }
}