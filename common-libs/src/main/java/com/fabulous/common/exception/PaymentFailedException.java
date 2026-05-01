package com.fabulous.common.exception;

public class PaymentFailedException extends BusinessException {
    public PaymentFailedException(String message) {
        super(message);
    }
}