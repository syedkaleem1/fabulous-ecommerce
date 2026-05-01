package com.fabulous.payment.gateway;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.Random;
import java.util.UUID;

@Component
@Slf4j
public class MockPaymentGateway {

    @Value("${payment.gateway.success-rate:0.9}")
    private double successRate;

    @Value("${payment.gateway.timeout-ms:2000}")
    private long timeoutMs;

    private final Random random = new Random();

    public PaymentCallbackDto processPayment(UUID orderId, double amount) {
        // Simulate network delay
        try {
            Thread.sleep(timeoutMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        boolean success = random.nextDouble() < successRate;
        String transactionId = UUID.randomUUID().toString();

        PaymentCallbackDto result = new PaymentCallbackDto();
        result.setOrderId(orderId);
        result.setSuccess(success);
        result.setTransactionId(success ? transactionId : null);
        result.setMessage(success ? "Payment processed successfully" : "Payment failed (insufficient funds)");

        log.info("Mock payment for order {}: {}", orderId, result.getMessage());
        return result;
    }
}
