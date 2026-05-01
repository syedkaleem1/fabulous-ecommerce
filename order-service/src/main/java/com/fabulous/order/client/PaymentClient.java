package com.fabulous.order.client;



import com.fabulous.order.dto.request.ChargeRequest;
import com.fabulous.order.dto.response.ChargeResponse;
import com.fabulous.order.exception.PaymentDeclinedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

/**
 * HTTP client: Order Service → Payment Service (port 8083)
 */
@Component
public class PaymentClient {

    private static final Logger log = LoggerFactory.getLogger(PaymentClient.class);

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public PaymentClient(
            RestTemplate restTemplate,
            @Value("${services.payment.url}") String baseUrl
    ) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    /** POST http://payment-service:8083/api/payments/charge */
    public ChargeResponse charge(ChargeRequest request) {
        log.info("[ORDER→PAYMENT] POST /charge orderId={} amount={}",
                request.orderId(), request.amount());
        try {
            ResponseEntity<ChargeResponse> resp = restTemplate.postForEntity(
                    baseUrl + "/api/payments/charge", request, ChargeResponse.class);

            ChargeResponse body = resp.getBody();
            if (body == null || !body.success()) {
                String declineCode = body != null ? body.declineCode() : "unknown";
                String msg = body != null ? body.message() : "No response from payment service";
                log.warn("[ORDER→PAYMENT] charge FAILED: {} (code={})", msg, declineCode);
                throw new PaymentDeclinedException(msg, declineCode);
            }
            log.info("[ORDER→PAYMENT] charge SUCCESS orderId={} intentId={}",
                    request.orderId(), body.paymentIntentId());
            return body;

        } catch (HttpClientErrorException ex) {
            throw new PaymentDeclinedException("Payment service error: " + ex.getMessage(), "http_error");
        }
    }
}
