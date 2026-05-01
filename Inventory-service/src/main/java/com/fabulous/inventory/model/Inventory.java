package com.fabulous.inventory.model;

import jakarta.persistence.*;

import java.time.Instant;

/**
 * Inventory Service's own view of a product.
 * Only tracks stock — price/images/description live in a separate catalog service.
 * No cross-service entity sharing.
 */
@Entity
@Table(name = "inventory")
public class Inventory {

    @Id
    @Column(name = "product_id")
    private Long productId;        // references product catalog (no FK — different service)

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(name = "stock_quantity", nullable = false)
    private int stockQuantity;

    @Column(name = "reserved_quantity", nullable = false)
    private int reservedQuantity = 0;

    @Version                       // optimistic locking fallback
    private Long version;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @PrePersist @PreUpdate
    protected void onUpdate() { updatedAt = Instant.now(); }

    public int getAvailableQuantity() {
        return stockQuantity - reservedQuantity;
    }

    public Long getProductId()              { return productId; }
    public void setProductId(Long v)        { this.productId = v; }
    public String getProductName()          { return productName; }
    public void setProductName(String v)    { this.productName = v; }
    public int getStockQuantity()           { return stockQuantity; }
    public void setStockQuantity(int v)     { this.stockQuantity = v; }
    public int getReservedQuantity()        { return reservedQuantity; }
    public void setReservedQuantity(int v)  { this.reservedQuantity = v; }
    public Long getVersion()                { return version; }
    public Instant getUpdatedAt()           { return updatedAt; }
}

