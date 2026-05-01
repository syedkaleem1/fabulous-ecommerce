package com.fabulous.payment.controller;

import com.fabulous.payment.dto.ChargeRequest;
import com.fabulous.payment.dto.ChargeResponse;
import com.fabulous.payment.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Payment Service REST API — port 8083.
 *
 * Called exclusively by Order Service saga orchestrator (internal network).
 * Port 8083 must NOT be exposed publicly — firewall to internal only.
 *
 * POST /api/payments/charge  → saga Step 3
 */
@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/charge")
    public ResponseEntity<ChargeResponse> charge(@RequestBody ChargeRequest request) {
        return ResponseEntity.ok(paymentService.charge(request));
    }
}
