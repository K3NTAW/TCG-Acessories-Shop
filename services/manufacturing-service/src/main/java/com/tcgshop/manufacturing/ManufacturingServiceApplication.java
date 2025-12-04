package com.tcgshop.manufacturing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

/**
 * Manufacturing Service
 * 
 * Manages 3D print jobs for orders.
 * Consumes order-created events and creates print jobs.
 */
@SpringBootApplication
@EnableKafka
public class ManufacturingServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ManufacturingServiceApplication.class, args);
    }
}

