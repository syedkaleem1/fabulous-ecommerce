package com.fabulous.inventory.service;

import com.fabulous.inventory.dto.InventoryResponse;
import com.fabulous.inventory.dto.ReleaseRequest;
import com.fabulous.inventory.dto.ReserveRequest;
import com.fabulous.inventory.model.Inventory;
import com.fabulous.inventory.repository.InventoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handles stock reservation and compensating release.
 *
 * All reservation attempts acquire a PESSIMISTIC_WRITE row lock so
 * concurrent orders for the same product serialize correctly.
 * If any line item fails, the transaction rolls back — no partial reserves.
 */
@Service
public class InventoryService {

    private static final Logger log = LoggerFactory.getLogger(InventoryService.class);

    private final InventoryRepository repo;

    public InventoryService(InventoryRepository repo) {
        this.repo = repo;
    }

    /**
     * Reserve stock for every item in the order atomically.
     * Called by Order Service saga Step 2.
     */
    @Transactional
    public InventoryResponse reserve(ReserveRequest request) {
        for (var line : request.items()) {
            Inventory inv = repo.findByProductIdForUpdate(line.productId()).orElse(null);

            if (inv == null) {
                log.warn("Product {} not found for orderId={}", line.productId(), request.orderId());
                return InventoryResponse.fail(
                        "Product not found: " + line.productId(),
                        String.valueOf(line.productId()));
            }

            if (inv.getAvailableQuantity() < line.quantity()) {
                log.warn("Insufficient stock productId={} available={} requested={} orderId={}",
                        line.productId(), inv.getAvailableQuantity(), line.quantity(), request.orderId());
                return InventoryResponse.fail(
                        "Insufficient stock for: " + inv.getProductName(),
                        String.valueOf(line.productId()));
            }

            inv.setReservedQuantity(inv.getReservedQuantity() + line.quantity());
            repo.save(inv);
            log.info("Reserved {} of productId={} orderId={}",
                    line.quantity(), line.productId(), request.orderId());
        }
        return InventoryResponse.ok();
    }

    /**
     * Release reserved stock — compensating transaction when payment fails.
     * Each product is updated independently; partial failures are logged
     * for manual reconciliation.
     */
    @Transactional
    public void release(ReleaseRequest request) {
        for (var line : request.items()) {
            repo.findByProductIdForUpdate(line.productId()).ifPresentOrElse(inv -> {
                int released = Math.min(line.quantity(), inv.getReservedQuantity());
                inv.setReservedQuantity(inv.getReservedQuantity() - released);
                repo.save(inv);
                log.info("Released {} of productId={} orderId={}",
                        released, line.productId(), request.orderId());
            }, () -> log.error(
                    "COMPENSATION WARNING: productId={} not found during release orderId={} — reconcile manually",
                    line.productId(), request.orderId()));
        }
    }
}
