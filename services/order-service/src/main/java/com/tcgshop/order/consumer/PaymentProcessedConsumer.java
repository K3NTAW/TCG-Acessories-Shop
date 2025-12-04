package com.tcgshop.order.consumer;

import com.tcgshop.order.model.Order;
import com.tcgshop.order.model.OrderStatus;
import com.tcgshop.order.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Kafka Consumer for payment-processed events
 */
@Component
public class PaymentProcessedConsumer {

    private final OrderRepository orderRepository;

    @Autowired
    public PaymentProcessedConsumer(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @KafkaListener(topics = "payment-processed", groupId = "order-service")
    public void handlePaymentProcessed(Map<String, Object> event) {
        Long orderId = Long.valueOf(event.get("orderId").toString());
        String status = (String) event.get("status");

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

        if ("COMPLETED".equals(status)) {
            order.setStatus(OrderStatus.PAYMENT_COMPLETED);
        } else {
            order.setStatus(OrderStatus.PAYMENT_PENDING);
        }

        orderRepository.save(order);
    }
}

