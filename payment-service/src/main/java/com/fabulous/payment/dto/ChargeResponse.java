package com.fabulous.payment.dto;

public record ChargeResponse(
        boolean success,
        String paymentIntentId,
        String chargeId,
        String declineCode,
        String message
) {
    public static ChargeResponse success(String paymentIntentId, String chargeId) {
        return new ChargeResponse(true, paymentIntentId, chargeId, null, "Payment successful");
    }
    public static ChargeResponse failure(String declineCode, String message) {
        return new ChargeResponse(false, null, null, declineCode, message);
    }
}