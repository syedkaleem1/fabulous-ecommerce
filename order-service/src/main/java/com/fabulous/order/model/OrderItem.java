package com.fabulous.order.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    /** Product ID only — no cross-service FK. Product details are snapshot-stored. */
    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(nullable = false)
    private int quantity;

    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal;

    public Long getId()                     { return id; }
    public Order getOrder()                 { return order; }
    public void setOrder(Order v)           { this.order = v; }
    public Long getProductId()              { return productId; }
    public void setProductId(Long v)        { this.productId = v; }
    public String getProductName()          { return productName; }
    public void setProductName(String v)    { this.productName = v; }
    public int getQuantity()                { return quantity; }
    public void setQuantity(int v)          { this.quantity = v; }
    public BigDecimal getUnitPrice()        { return unitPrice; }
    public void setUnitPrice(BigDecimal v)  { this.unitPrice = v; }
    public BigDecimal getSubtotal()         { return subtotal; }
    public void setSubtotal(BigDecimal v)   { this.subtotal = v; }
}
