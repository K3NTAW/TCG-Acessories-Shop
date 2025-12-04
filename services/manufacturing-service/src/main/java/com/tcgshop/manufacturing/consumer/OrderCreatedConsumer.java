package com.tcgshop.manufacturing.consumer;

import com.tcgshop.manufacturing.service.ManufacturingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Kafka Consumer for order-created events
 */
@Component
public class OrderCreatedConsumer {

    private final ManufacturingService manufacturingService;

    @Autowired
    public OrderCreatedConsumer(ManufacturingService manufacturingService) {
        this.manufacturingService = manufacturingService;
    }

    @KafkaListener(topics = "order-created", groupId = "manufacturing-service")
    public void handleOrderCreated(Map<String, Object> event) {
        Long orderId = Long.valueOf(event.get("orderId").toString());
        String orderNumber = (String) event.get("orderNumber");

        // Create print job for the order
        manufacturingService.createPrintJob(orderId, orderNumber);
    }
}

