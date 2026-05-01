//package com.fabulous.order.client;
//
//import com.fabulous.order.dto.request.StockReservationRequest;
//import com.fabulous.order.dto.response.StockReservationResponse;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Component;
//import org.springframework.web.client.RestClient;
//import java.util.UUID;
//
//@Component
//@RequiredArgsConstructor
//public class InventoryServiceClient {
//
//    private final RestClient inventoryServiceRestClient;
//
//    public StockReservationResponse reserveStock(UUID orderId, Long productId, int quantity) {
//        StockReservationRequest request = new StockReservationRequest();
//        request.setOrderId(orderId);
//        request.setProductId(productId);
//        request.setQuantity(quantity);
//
//        return inventoryServiceRestClient.post()
//                .uri("/api/inventory/reserve")
//                .body(request)
//                .retrieve()
//                .body(StockReservationResponse.class);
//    }
//
//    public void confirmReservation(Long productId, int quantity) {
//        inventoryServiceRestClient.post()
//                .uri(uriBuilder -> uriBuilder
//                        .path("/api/inventory/confirm")
//                        .queryParam("productId", productId)
//                        .queryParam("quantity", quantity)
//                        .build())
//                .retrieve()
//                .toBodilessEntity();
//    }
//
//    public void releaseReservation(Long productId, int quantity) {
//        inventoryServiceRestClient.post()
//                .uri(uriBuilder -> uriBuilder
//                        .path("/api/inventory/release")
//                        .queryParam("productId", productId)
//                        .queryParam("quantity", quantity)
//                        .build())
//                .retrieve()
//                .toBodilessEntity();
//    }
//}