//package com.fabulous.order.client;
//
//import com.fabulous.order.dto.request.PaymentRequest;
//import com.fabulous.order.dto.response.PaymentResponse;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Component;
//import org.springframework.web.client.RestClient;
//
//@Component
//@RequiredArgsConstructor
//public class PaymentServiceClient {
//
//    private final RestClient paymentServiceRestClient;
//
//    public PaymentResponse processPayment(PaymentRequest request) {
//        return paymentServiceRestClient.post()
//                .uri("/api/payments/process")
//                .body(request)
//                .retrieve()
//                .body(PaymentResponse.class);
//    }
//}