package com.fabulous.common.dto.order.request;
// ── Incoming from client ──────────────────────────────────────────────────────

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

// POST /api/orders body
public record CreateOrderRequest(
        @NotEmpty List<@Valid OrderItemRequest> items,
        @NotNull @Valid ShippingAddress shippingAddress,
        @NotBlank String paymentMethodId
) {
    public record OrderItemRequest(
            @NotNull Long productId,
            @NotBlank String productName,
            @Min(1) int quantity,
            @NotNull java.math.BigDecimal unitPrice
    ) {}

    public record ShippingAddress(
            @NotBlank String street,
            @NotBlank String city,
            @NotBlank String state,
            @NotBlank String zipCode,
            @NotBlank String country
    ) {}
}
