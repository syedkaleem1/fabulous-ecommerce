package com.fabulous.order.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_number", nullable = false, unique = true)
    private String orderNumber;

    /** User identity comes from the JWT forwarded by the gateway — no User FK needed */
    @Column(name = "user_email", nullable = false)
    private String userEmail;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status = OrderStatus.PENDING;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "shipping_street")  private String shippingStreet;
    @Column(name = "shipping_city")    private String shippingCity;
    @Column(name = "shipping_state")   private String shippingState;
    @Column(name = "shipping_zip")     private String shippingZip;
    @Column(name = "shipping_country") private String shippingCountry;

    /** Populated on saga success */
    @Column(name = "payment_intent_id") private String paymentIntentId;
    @Column(name = "payment_charge_id") private String paymentChargeId;

    @Column(name = "created_at", updatable = false) private Instant createdAt;
    @Column(name = "updated_at")                    private Instant updatedAt;

    @PrePersist  protected void onCreate() { createdAt = updatedAt = Instant.now(); }
    @PreUpdate   protected void onUpdate() { updatedAt = Instant.now(); }

    // ── Getters & Setters ─────────────────────────────────────────────────────
    public Long getId()                        { return id; }
    public String getOrderNumber()             { return orderNumber; }
    public void setOrderNumber(String v)       { this.orderNumber = v; }
    public String getUserEmail()               { return userEmail; }
    public void setUserEmail(String v)         { this.userEmail = v; }
    public OrderStatus getStatus()             { return status; }
    public void setStatus(OrderStatus v)       { this.status = v; }
    public List<OrderItem> getItems()          { return items; }
    public void setItems(List<OrderItem> v)    { this.items = v; }
    public BigDecimal getTotalAmount()         { return totalAmount; }
    public void setTotalAmount(BigDecimal v)   { this.totalAmount = v; }
    public String getShippingStreet()          { return shippingStreet; }
    public void setShippingStreet(String v)    { this.shippingStreet = v; }
    public String getShippingCity()            { return shippingCity; }
    public void setShippingCity(String v)      { this.shippingCity = v; }
    public String getShippingState()           { return shippingState; }
    public void setShippingState(String v)     { this.shippingState = v; }
    public String getShippingZip()             { return shippingZip; }
    public void setShippingZip(String v)       { this.shippingZip = v; }
    public String getShippingCountry()         { return shippingCountry; }
    public void setShippingCountry(String v)   { this.shippingCountry = v; }
    public String getPaymentIntentId()         { return paymentIntentId; }
    public void setPaymentIntentId(String v)   { this.paymentIntentId = v; }
    public String getPaymentChargeId()         { return paymentChargeId; }
    public void setPaymentChargeId(String v)   { this.paymentChargeId = v; }
    public Instant getCreatedAt()              { return createdAt; }
    public Instant getUpdatedAt()              { return updatedAt; }
}
