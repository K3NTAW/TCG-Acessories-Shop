package com.tcgshop.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Product Catalog Service
 * 
 * Manages product information for 3D printed TCG accessories.
 * Handles CRUD operations for products and product variants.
 */
@SpringBootApplication
public class ProductCatalogApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductCatalogApplication.class, args);
    }
}

