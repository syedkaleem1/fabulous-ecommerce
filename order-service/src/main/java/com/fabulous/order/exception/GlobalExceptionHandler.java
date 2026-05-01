package com.fabulous.order.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // [Inventory fails] branch → 400 Out of Stock
    @ExceptionHandler(InventoryUnavailableException.class)
    public ProblemDetail handleInventory(InventoryUnavailableException ex) {
        log.warn("Inventory failed: {}", ex.getMessage());
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        pd.setTitle("Out of Stock");
        pd.setType(URI.create("/errors/out-of-stock"));
        pd.setProperty("failedProductId", ex.getFailedProductId());
        pd.setProperty("timestamp", Instant.now());
        return pd;
    }

    // [Payment fails] branch → 402 Payment Error
    @ExceptionHandler(PaymentDeclinedException.class)
    public ProblemDetail handlePayment(PaymentDeclinedException ex) {
        log.warn("Payment declined: {} ({})", ex.getMessage(), ex.getDeclineCode());
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.PAYMENT_REQUIRED, ex.getMessage());
        pd.setTitle("Payment Error");
        pd.setType(URI.create("/errors/payment-declined"));
        pd.setProperty("declineCode", ex.getDeclineCode());
        pd.setProperty("timestamp", Instant.now());
        return pd;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        fe -> fe.getField(),
                        fe -> fe.getDefaultMessage() != null ? fe.getDefaultMessage() : "invalid",
                        (a, b) -> a));
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Validation failed");
        pd.setTitle("Validation Error");
        pd.setType(URI.create("/errors/validation"));
        pd.setProperty("fieldErrors", errors);
        return pd;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGeneric(Exception ex) {
        log.error("Unhandled exception", ex);
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error");
        pd.setTitle("Internal Server Error");
        return pd;
    }
}
