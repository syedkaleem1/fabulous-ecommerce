package com.fabulous.payment.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * Payment Service's own audit record of every charge attempt.
 * Stores Stripe IDs for reconciliation and refund workflows.
 * No FK to Order Service tables — different schema, different service.
 */
@Entity
@Table(name = "payment_records")
public class PaymentRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id", nullable = false)
    private String orderId;            // string ref to order-service order, no FK

    @Column(name = "payment_intent_id", unique = true)
    private String paymentIntentId;

    @Column(name = "charge_id")
    private String chargeId;

    @Column(name = "payment_method_id")
    private String paymentMethodId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 3)
    private String currency;

    @Column(name = "customer_email")
    private String customerEmail;

    @Column(name = "decline_code")
    private String declineCode;

    @Column(name = "failure_message", columnDefinition = "TEXT")
    private String failureMessage;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() { createdAt = Instant.now(); }

    public Long getId()                         { return id; }
    public String getOrderId()                  { return orderId; }
    public void setOrderId(String v)            { this.orderId = v; }
    public String getPaymentIntentId()          { return paymentIntentId; }
    public void setPaymentIntentId(String v)    { this.paymentIntentId = v; }
    public String getChargeId()                 { return chargeId; }
    public void setChargeId(String v)           { this.chargeId = v; }
    public String getPaymentMethodId()          { return paymentMethodId; }
    public void setPaymentMethodId(String v)    { this.paymentMethodId = v; }
    public PaymentStatus getStatus()            { return status; }
    public void setStatus(PaymentStatus v)      { this.status = v; }
    public BigDecimal getAmount()               { return amount; }
    public void setAmount(BigDecimal v)         { this.amount = v; }
    public String getCurrency()                 { return currency; }
    public void setCurrency(String v)           { this.currency = v; }
    public String getCustomerEmail()            { return customerEmail; }
    public void setCustomerEmail(String v)      { this.customerEmail = v; }
    public String getDeclineCode()              { return declineCode; }
    public void setDeclineCode(String v)        { this.declineCode = v; }
    public String getFailureMessage()           { return failureMessage; }
    public void setFailureMessage(String v)     { this.failureMessage = v; }
    public Instant getCreatedAt()               { return createdAt; }
}
