package com.fabulous.order.service.orchestrator;

import com.fabulous.order.client.InventoryClient;
import com.fabulous.order.client.PaymentClient;
import com.fabulous.order.dto.request.*;
import com.fabulous.order.dto.response.ChargeResponse;
import com.fabulous.order.exception.InventoryUnavailableException;
import com.fabulous.order.exception.PaymentDeclinedException;
import com.fabulous.order.model.Order;
import com.fabulous.order.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Orchestration-based Saga — exact implementation of the sequence diagram.
 *
 * This service drives the entire order flow by making HTTP calls to two
 * independently deployed microservices:
 *
 *   Step 1 │ Persist order as PENDING (own DB)
 *   Step 2 │ HTTP POST → inventory-service:8082/api/inventory/reserve
 *           │   [Inventory fails] → cancel order → throw 400
 *   Step 3 │ HTTP POST → payment-service:8083/api/payments/charge
 *           │   [Payment fails]   → HTTP POST → inventory-service:8082/api/inventory/release
 *           │                     → cancel order → throw 402
 *           │   [Success]         → confirm order → return 201
 */
@Service
public class OrderSagaOrchestrator {

    private static final Logger log = LoggerFactory.getLogger(OrderSagaOrchestrator.class);

    private final OrderService orderService;
    private final InventoryClient inventoryClient;
    private final PaymentClient paymentClient;

    public OrderSagaOrchestrator(
            OrderService orderService,
            InventoryClient inventoryClient,
            PaymentClient paymentClient
    ) {
        this.orderService = orderService;
        this.inventoryClient = inventoryClient;
        this.paymentClient = paymentClient;
    }

    public Order execute(CreateOrderRequest request, String userEmail) {
        // ── Step 1: Create order PENDING ──────────────────────────────────────
        Order order = orderService.createPending(request, userEmail);
        String orderId = String.valueOf(order.getId());
        log.info("SAGA START orderId={} user={}", orderId, userEmail);

        // ── Step 2: Reserve inventory (HTTP → inventory-service) ──────────────
        ReserveItemsRequest reserveReq = new ReserveItemsRequest(
                orderId,
                request.items().stream()
                        .map(i -> new ReserveItemsRequest.LineItem(i.productId(), i.quantity()))
                        .toList()
        );

        try {
            inventoryClient.reserve(reserveReq);                       // network call
        } catch (InventoryUnavailableException ex) {
            // [Inventory fails] — cancel and surface error, NO payment was attempted
            log.warn("SAGA STEP 2 FAILED (inventory) orderId={}: {}", orderId, ex.getMessage());
            orderService.cancel(order.getId());
            throw ex;                                                   // → 400 Out of Stock
        }

        // ── Step 3: Charge payment (HTTP → payment-service) ───────────────────
        ChargeRequest chargeReq = new ChargeRequest(
                orderId,
                request.paymentMethodId(),
                order.getTotalAmount(),
                "usd",
                userEmail,
                "ShopForge Order #" + order.getOrderNumber()
        );

        try {
            ChargeResponse chargeResp = paymentClient.charge(chargeReq);  // network call

            // [Success] — confirm order, saga complete
            Order confirmed = orderService.confirm(
                    order.getId(),
                    chargeResp.paymentIntentId(),
                    chargeResp.chargeId()
            );
            log.info("SAGA COMPLETE orderId={} intentId={}", orderId, chargeResp.paymentIntentId());
            return confirmed;

        } catch (PaymentDeclinedException ex) {
            // [Payment fails] — run compensating transaction then cancel
            log.warn("SAGA STEP 3 FAILED (payment) orderId={}: {}", orderId, ex.getMessage());

            ReleaseItemsRequest releaseReq = new ReleaseItemsRequest(
                    orderId,
                    request.items().stream()
                            .map(i -> new ReleaseItemsRequest.LineItem(i.productId(), i.quantity()))
                            .toList()
            );
            inventoryClient.release(releaseReq);                       // compensating network call
            orderService.cancel(order.getId());
            throw ex;                                                   // → 402 Payment Error
        }
    }
}

