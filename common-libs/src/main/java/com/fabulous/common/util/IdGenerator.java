package com.fabulous.common.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class IdGenerator {

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    public static String generateOrderId() {
        return "ORD-" + generateId();
    }

    public static String generatePaymentId() {
        return "PAY-" + generateId();
    }

    public static String generateTransactionId() {
        return "TXN-" + generateId();
    }

    public static String generateProductId() {
        return "PROD-" + generateId();
    }

    public static String generateCustomerId() {
        return "CUST-" + generateId();
    }

    private static String generateId() {
        String timestamp = LocalDateTime.now().format(DATE_FORMAT);
        String uuid = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return timestamp + "-" + uuid;
    }

    public static String generateUUID() {
        return UUID.randomUUID().toString();
    }
}
