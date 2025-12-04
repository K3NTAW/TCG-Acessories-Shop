package com.tcgshop.payment.service;

import com.tcgshop.payment.model.Payment;
import com.tcgshop.payment.model.PaymentStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Payment Service (Mockup)
 * Simulates payment processing
 */
@Service
public class PaymentService {

    private final AtomicLong idGenerator = new AtomicLong(1);
    private final Map<Long, Payment> payments = new ConcurrentHashMap<>();
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    public PaymentService(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public Payment processPayment(Map<String, Object> paymentRequest) {
        Payment payment = new Payment();
        payment.setId(idGenerator.getAndIncrement());
        payment.setOrderId(Long.valueOf(paymentRequest.get("orderId").toString()));
        payment.setOrderNumber((String) paymentRequest.get("orderNumber"));
        payment.setAmount(new BigDecimal(paymentRequest.get("amount").toString()));
        payment.setPaymentMethod("CREDIT_CARD"); // Mock
        payment.setStatus(PaymentStatus.PROCESSING);
        payment.setCreatedAt(LocalDateTime.now());

        payments.put(payment.getId(), payment);

        // Simulate payment processing (mock - always succeeds)
        try {
            Thread.sleep(500); // Simulate processing time
            payment.setStatus(PaymentStatus.COMPLETED);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            payment.setStatus(PaymentStatus.FAILED);
        }

        payments.put(payment.getId(), payment);

        // Publish Kafka event: payment-processed
        Map<String, Object> event = new HashMap<>();
        event.put("paymentId", payment.getId());
        event.put("orderId", payment.getOrderId());
        event.put("orderNumber", payment.getOrderNumber());
        event.put("status", payment.getStatus().name());
        event.put("amount", payment.getAmount());
        kafkaTemplate.send("payment-processed", event);

        return payment;
    }

    public Payment getPayment(Long id) {
        return payments.get(id);
    }

    public Payment getPaymentByOrderId(Long orderId) {
        return payments.values().stream()
                .filter(p -> p.getOrderId().equals(orderId))
                .findFirst()
                .orElse(null);
    }
}

