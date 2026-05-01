package com.fabulous.order.exception;

public class InventoryUnavailableException extends RuntimeException {
    private final String failedProductId;
    public InventoryUnavailableException(String message, String failedProductId) {
        super(message);
        this.failedProductId = failedProductId;
    }
    public String getFailedProductId() { return failedProductId; }
}

