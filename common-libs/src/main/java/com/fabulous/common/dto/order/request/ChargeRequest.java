package com.fabulous.common.dto.order.request;

import java.math.BigDecimal;

// Sent to: POST http://payment-service:8083/api/payments/charge
public record ChargeRequest(
        String orderId,
        String paymentMethodId,
        BigDecimal amount,
        String currency,
        String customerEmail,
        String description
) {}