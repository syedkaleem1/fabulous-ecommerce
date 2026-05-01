package com.fabulous.common.constants;

public class AppConstants {

    // Order Status
    public static final String ORDER_PENDING = "PENDING";
    public static final String ORDER_INVENTORY_CHECKED = "INVENTORY_CHECKED";
    public static final String ORDER_PAYMENT_PROCESSING = "PAYMENT_PROCESSING";
    public static final String ORDER_COMPLETED = "COMPLETED";
    public static final String ORDER_FAILED = "FAILED";
    public static final String ORDER_CANCELLED = "CANCELLED";

    // Payment Status
    public static final String PAYMENT_PENDING = "PENDING";
    public static final String PAYMENT_PROCESSING = "PROCESSING";
    public static final String PAYMENT_SUCCESS = "SUCCESS";
    public static final String PAYMENT_FAILED = "FAILED";
    public static final String PAYMENT_REFUNDED = "REFUNDED";

    // Payment Methods
    public static final String PAYMENT_CREDIT_CARD = "CREDIT_CARD";
    public static final String PAYMENT_DEBIT_CARD = "DEBIT_CARD";
    public static final String PAYMENT_UPI = "UPI";
    public static final String PAYMENT_NET_BANKING = "NET_BANKING";
    public static final String PAYMENT_WALLET = "WALLET";

    // Response Messages
    public static final String MSG_SUCCESS = "Operation completed successfully";
    public static final String MSG_CREATED = "Resource created successfully";
    public static final String MSG_UPDATED = "Resource updated successfully";
    public static final String MSG_DELETED = "Resource deleted successfully";
    public static final String MSG_NOT_FOUND = "Resource not found";
    public static final String MSG_BAD_REQUEST = "Invalid request";

    private AppConstants() {
        // Private constructor
    }
}