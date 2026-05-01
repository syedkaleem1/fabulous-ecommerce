package com.fabulous.payment.repository;

import com.fabulous.payment.model.PaymentRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

import java.util.List;

public interface PaymentRepository extends JpaRepository<PaymentRecord, Long> {
    Optional<PaymentRecord> findByPaymentIntentId(String paymentIntentId);
    List<PaymentRecord> findByOrderId(String orderId);
}