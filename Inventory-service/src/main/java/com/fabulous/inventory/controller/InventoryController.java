package com.fabulous.inventory.controller;

import com.fabulous.inventory.dto.InventoryResponse;
import com.fabulous.inventory.dto.ReleaseRequest;
import com.fabulous.inventory.dto.ReserveRequest;
import com.fabulous.inventory.service.InventoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Inventory Service REST API — port 8082.
 * <p>
 * These endpoints are called by the Order Service saga orchestrator,
 * NOT by end clients. In production, port 8082 must be firewalled to
 * internal network only — only the API Gateway (port 8000) is public.
 * <p>
 * POST /api/inventory/reserve  → saga Step 2
 * POST /api/inventory/release  → compensating transaction
 */
@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping("/reserve")
    public ResponseEntity<InventoryResponse> reserve(@RequestBody ReserveRequest request) {
        return ResponseEntity.ok(inventoryService.reserve(request));
    }

    @PostMapping("/release")
    public ResponseEntity<InventoryResponse> release(@RequestBody ReleaseRequest request) {
        inventoryService.release(request);
        return ResponseEntity.ok(InventoryResponse.ok());
    }
}
