package com.fabulous.common.dto.order.response;

// Received from Payment Service
public record ChargeResponse(
        boolean success,
        String paymentIntentId,
        String chargeId,
        String declineCode,
        String message
) {}

