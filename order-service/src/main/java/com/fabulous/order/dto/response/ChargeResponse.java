package com.fabulous.order.dto.response;

// Received from Payment Service
public record ChargeResponse(
        boolean success,
        String paymentIntentId,
        String chargeId,
        String declineCode,
        String message
) {}

