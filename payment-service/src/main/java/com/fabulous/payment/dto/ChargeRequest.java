package com.fabulous.payment.dto;

import java.math.BigDecimal;

public record ChargeRequest(
        String orderId,
        String paymentMethodId,
        BigDecimal amount,
        String currency,
        String customerEmail,
        String description
) {}