package com.fabulous.payment.service;

import com.fabulous.payment.dto.ChargeRequest;
import com.fabulous.payment.dto.ChargeResponse;
import com.fabulous.payment.model.PaymentRecord;
import com.fabulous.payment.model.PaymentStatus;
import com.fabulous.payment.repository.PaymentRepository;
import com.stripe.Stripe;
import com.stripe.exception.CardException;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Processes charges via Stripe PaymentIntent API.
 * Every attempt — success or failure — is persisted to payment_records
 * for audit, reconciliation, and refund workflows.
 */
@Service
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    @Value("${stripe.api.key}")
    private String stripeApiKey;

    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeApiKey;
        log.info("Stripe SDK initialised");
    }

    @Transactional
    public ChargeResponse charge(ChargeRequest request) {
        log.info("[PAYMENT] Charging orderId={} amount={} {}",
                request.orderId(), request.amount(), request.currency());

        PaymentRecord record = new PaymentRecord();
        record.setOrderId(request.orderId());
        record.setPaymentMethodId(request.paymentMethodId());
        record.setAmount(request.amount());
        record.setCurrency(request.currency());
        record.setCustomerEmail(request.customerEmail());

        try {
            long amountCents = request.amount()
                    .multiply(java.math.BigDecimal.valueOf(100))
                    .longValueExact();

            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(amountCents)
                    .setCurrency(request.currency())
                    .setPaymentMethod(request.paymentMethodId())
                    .setConfirm(true)
                    .setDescription(request.description())
                    .setReceiptEmail(request.customerEmail())
                    .putMetadata("orderId", request.orderId())
                    .setReturnUrl("https://fabulous.com/orders/" + request.orderId())
                    .build();

            PaymentIntent intent = PaymentIntent.create(params);
            String chargeId = intent.getLatestCharge();

            record.setPaymentIntentId(intent.getId());
            record.setChargeId(chargeId);
            record.setStatus(PaymentStatus.SUCCESS);
            paymentRepository.save(record);

            log.info("[PAYMENT] SUCCESS orderId={} intentId={}", request.orderId(), intent.getId());
            return ChargeResponse.success(intent.getId(), chargeId);

        } catch (CardException ex) {
            // Card declined — maps to [Payment fails] saga branch
            log.warn("[PAYMENT] DECLINED orderId={} code={}", request.orderId(), ex.getDeclineCode());
            record.setStatus(PaymentStatus.DECLINED);
            record.setDeclineCode(ex.getDeclineCode());
            record.setFailureMessage(ex.getMessage());
            paymentRepository.save(record);
            return ChargeResponse.failure(ex.getDeclineCode(), ex.getMessage());

        } catch (StripeException ex) {
            log.error("[PAYMENT] STRIPE ERROR orderId={}: {}", request.orderId(), ex.getMessage());
            record.setStatus(PaymentStatus.ERROR);
            record.setFailureMessage(ex.getMessage());
            paymentRepository.save(record);
            return ChargeResponse.failure("stripe_error", ex.getMessage());
        }
    }
}
