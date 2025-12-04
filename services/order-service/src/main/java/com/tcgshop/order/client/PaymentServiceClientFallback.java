package com.tcgshop.order.client;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Fallback implementation for Payment Service
 * Used when Circuit Breaker is open
 */
@Component
public class PaymentServiceClientFallback implements PaymentServiceClient {

    @Override
    public Map<String, Object> processPayment(Map<String, Object> paymentRequest) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "PENDING");
        response.put("message", "Payment service temporarily unavailable. Order marked as payment pending.");
        response.put("orderId", paymentRequest.get("orderId"));
        return response;
    }
}

