package com.tcgshop.payment.service;

import com.tcgshop.payment.model.Payment;
import com.tcgshop.payment.model.PaymentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private PaymentService paymentService;

    private Map<String, Object> paymentRequest;

    @BeforeEach
    void setUp() {
        paymentRequest = new HashMap<>();
        paymentRequest.put("orderId", 1L);
        paymentRequest.put("orderNumber", "ORD-123");
        paymentRequest.put("amount", new BigDecimal("59.98"));
        paymentRequest.put("customerId", 1L);
    }

    @Test
    void testProcessPayment_Success() {
        // When
        Payment result = paymentService.processPayment(paymentRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getOrderId()).isEqualTo(1L);
        assertThat(result.getOrderNumber()).isEqualTo("ORD-123");
        assertThat(result.getAmount()).isEqualByComparingTo(new BigDecimal("59.98"));
        assertThat(result.getStatus()).isEqualTo(PaymentStatus.COMPLETED);
        
        verify(kafkaTemplate).send(eq("payment-processed"), any());
    }

    @Test
    void testProcessPayment_PublishesKafkaEvent() {
        // Given
        ArgumentCaptor<Map<String, Object>> eventCaptor = ArgumentCaptor.forClass(Map.class);

        // When
        Payment payment = paymentService.processPayment(paymentRequest);

        // Then
        verify(kafkaTemplate).send(eq("payment-processed"), eventCaptor.capture());
        Map<String, Object> event = eventCaptor.getValue();
        assertThat(event).containsKey("paymentId");
        assertThat(event).containsKey("orderId");
        assertThat(event).containsKey("orderNumber");
        assertThat(event).containsKey("status");
        assertThat(event).containsKey("amount");
    }

    @Test
    void testGetPayment() {
        // Given
        Payment payment = paymentService.processPayment(paymentRequest);
        Long paymentId = payment.getId();

        // When
        Payment result = paymentService.getPayment(paymentId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(paymentId);
    }

    @Test
    void testGetPayment_NotFound() {
        // When
        Payment result = paymentService.getPayment(999L);

        // Then
        assertThat(result).isNull();
    }

    @Test
    void testGetPaymentByOrderId() {
        // Given
        Payment payment = paymentService.processPayment(paymentRequest);

        // When
        Payment result = paymentService.getPaymentByOrderId(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getOrderId()).isEqualTo(1L);
    }

    @Test
    void testGetPaymentByOrderId_NotFound() {
        // When
        Payment result = paymentService.getPaymentByOrderId(999L);

        // Then
        assertThat(result).isNull();
    }
}

