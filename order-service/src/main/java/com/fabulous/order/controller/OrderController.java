package com.fabulous.order.controller;

import com.fabulous.order.dto.request.CreateOrderRequest;
import com.fabulous.order.dto.response.OrderResponse;
import com.fabulous.order.model.Order;
import com.fabulous.order.service.orchestrator.OrderSagaOrchestrator;
import com.fabulous.order.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

/**
 * REST controller for the Order Service (port 8081).
 *
 * All requests arrive via the API Gateway which has already:
 *   - Validated the JWT
 *   - Forwarded X-User-Email and X-User-Role as trusted headers
 *
 * This service does NOT re-validate JWTs — it trusts the gateway headers.
 * In production, restrict port 8081 to internal network only (not public).
 */
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderSagaOrchestrator saga;
    private final OrderService orderService;

    public OrderController(OrderSagaOrchestrator saga, OrderService orderService) {
        this.saga = saga;
        this.orderService = orderService;
    }

    /**
     * POST /api/orders
     * Triggers the full saga: PENDING → reserve → charge → CONFIRMED
     * Returns 201 Created on success, 400/402 on saga failure.
     */
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @Valid @RequestBody CreateOrderRequest request,
            @RequestHeader("X-User-Email") String userEmail   // set by gateway JwtAuthFilter
    ) {
        Order confirmed = saga.execute(request, userEmail);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(confirmed.getId())
                .toUri();

        return ResponseEntity
                .created(location)                            // 201 Created
                .body(OrderResponse.from(confirmed, "Order confirmed"));
    }

    /**
     * GET /api/orders
     * Returns the authenticated user's order history.
     */
    @GetMapping
    public Page<OrderResponse> getMyOrders(
            @RequestHeader("X-User-Email") String userEmail,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        return orderService.findByUser(userEmail, pageable)
                .map(o -> OrderResponse.from(o, null));
    }

    /**
     * GET /api/orders/{id}
     */
    @GetMapping("/{id}")
    public OrderResponse getOrder(
            @PathVariable Long id,
            @RequestHeader("X-User-Email") String userEmail,
            @RequestHeader("X-User-Role") String userRole
    ) {
        Order order = orderService.findById(id);

        boolean isOwner = order.getUserEmail().equals(userEmail);
        boolean isAdmin = "ADMIN".equals(userRole);
        if (!isOwner && !isAdmin) {
            throw new org.springframework.security.access.AccessDeniedException("Access denied");
        }

        return OrderResponse.from(order, null);
    }

    /** Admin: all orders */
    @GetMapping("/admin/all")
    public Page<OrderResponse> getAllOrders(
            @RequestHeader("X-User-Role") String role,
            @PageableDefault(size = 50) Pageable pageable
    ) {
        if (!"ADMIN".equals(role)) {
            throw new org.springframework.security.access.AccessDeniedException("Admin only");
        }
        return orderService.findAll(pageable).map(o -> OrderResponse.from(o, null));
    }
}
