# Quick Setup Guide for Remaining Services

Due to the large number of files, the remaining services (Order, Payment, Manufacturing, Review) follow the same pattern as the created services.

## Service Templates

Each service needs:
1. `pom.xml` - Maven dependencies
2. `src/main/java/com/tcgshop/{service}/{Service}Application.java` - Main class
3. `src/main/java/com/tcgshop/{service}/controller/` - REST Controllers
4. `src/main/java/com/tcgshop/{service}/service/` - Business logic
5. `src/main/java/com/tcgshop/{service}/repository/` - Data access
6. `src/main/java/com/tcgshop/{service}/model/` - Entities
7. `src/main/java/com/tcgshop/{service}/dto/` - DTOs
8. `src/main/resources/application.yml` - Configuration
9. `Dockerfile` - Container definition

## Order Service
- Port: 8084
- Database: MySQL (order_db)
- Features: Circuit Breaker (Resilience4j) for Payment Service calls
- Kafka: Producer (order-created), Consumer (payment-processed)

## Payment Service
- Port: 8085
- Database: In-Memory (Mockup)
- Features: Simulates payment processing
- Kafka: Producer (payment-processed)

## Manufacturing Service
- Port: 8086
- Database: MySQL/Mockup
- Features: Kafka Consumer (order-created), Producer (manufacturing-job-created)
- Manages 3D print jobs

## Review Service
- Port: 8087
- Database: MySQL/Mockup
- Features: Product reviews and ratings

All services follow the same structure as Product Catalog and Cart services.

