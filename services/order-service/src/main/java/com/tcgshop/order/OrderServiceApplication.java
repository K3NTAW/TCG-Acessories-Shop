package com.tcgshop.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.kafka.annotation.EnableKafka;

/**
 * Order Service
 * 
 * Manages orders with Circuit Breaker pattern for Payment Service calls.
 * Publishes and consumes Kafka events for order lifecycle.
 */
@SpringBootApplication
@EnableFeignClients
@EnableKafka
public class OrderServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }
}

