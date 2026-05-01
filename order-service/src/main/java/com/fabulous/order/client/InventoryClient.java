package com.fabulous.order.client;

import com.fabulous.order.dto.request.ReserveItemsRequest;
import com.fabulous.order.dto.request.ReleaseItemsRequest;
import com.fabulous.order.dto.response.InventoryResponse;
import com.fabulous.order.exception.InventoryUnavailableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

/**
 * HTTP client: Order Service → Inventory Service (port 8082)
 * Called by the saga orchestrator — never by the controller directly.
 */
@Component
public class InventoryClient {

    private static final Logger log = LoggerFactory.getLogger(InventoryClient.class);

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public InventoryClient(
            RestTemplate restTemplate,
            @Value("${services.inventory.url}") String baseUrl
    ) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    /** POST http://inventory-service:8082/api/inventory/reserve */
    public void reserve(ReserveItemsRequest request) {
        log.info("[ORDER→INVENTORY] POST /reserve orderId={}", request.orderId());
        try {
            ResponseEntity<InventoryResponse> resp = restTemplate.postForEntity(
                    baseUrl + "/api/inventory/reserve", request, InventoryResponse.class);

            InventoryResponse body = resp.getBody();
            if (body == null || !body.success()) {
                String msg = body != null ? body.message() : "No response from inventory service";
                String failedProduct = body != null ? body.failedProductId() : null;
                log.warn("[ORDER→INVENTORY] reserve FAILED: {}", msg);
                throw new InventoryUnavailableException(msg, failedProduct);
            }
            log.info("[ORDER→INVENTORY] reserve SUCCESS orderId={}", request.orderId());

        } catch (HttpClientErrorException ex) {
            throw new InventoryUnavailableException("Inventory service error: " + ex.getMessage(), null);
        }
    }

    /**
     * POST http://inventory-service:8082/api/inventory/release
     * Compensating transaction — best-effort, never throws.
     */
    public void release(ReleaseItemsRequest request) {
        log.info("[ORDER→INVENTORY] POST /release (compensating tx) orderId={}", request.orderId());
        try {
            restTemplate.postForEntity(
                    baseUrl + "/api/inventory/release", request, InventoryResponse.class);
            log.info("[ORDER→INVENTORY] release SUCCESS orderId={}", request.orderId());
        } catch (Exception ex) {
            log.error("[ORDER→INVENTORY] COMPENSATION FAILED for orderId={} — manual reconciliation needed: {}",
                    request.orderId(), ex.getMessage());
        }
    }
}

