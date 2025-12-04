# Setup Guide - TCG Accessories Shop

## Prerequisites

- Java 17+
- Maven 3.8+
- Docker & Docker Compose
- Node.js 18+ (for frontend)
- Git

## Quick Start

### 1. Start Infrastructure Services

```bash
cd docker
docker-compose up -d
```

This starts:
- MySQL databases (3 instances)
- Redis
- Kafka + Zookeeper

### 2. Start Microservices (in order)

#### Terminal 1: Eureka Server
```bash
cd services/eureka-server
mvn spring-boot:run
```

#### Terminal 2: API Gateway
```bash
cd services/api-gateway
mvn spring-boot:run
```

#### Terminal 3: Product Catalog Service
```bash
cd services/product-catalog-service
mvn spring-boot:run
```

#### Terminal 4: Cart Service
```bash
cd services/cart-service
mvn spring-boot:run
```

#### Terminal 5: Customer Service
```bash
cd services/customer-service
mvn spring-boot:run
```

### 3. Start Frontend

```bash
cd frontend/tcg-shop-frontend
npm install
npm run dev
```

## Service Ports

- Eureka Server: http://localhost:8761
- API Gateway: http://localhost:8080
- Product Catalog: http://localhost:8081
- Cart Service: http://localhost:8082
- Customer Service: http://localhost:8083
- Frontend: http://localhost:3000

## Testing

### Test Product Catalog API
```bash
curl http://localhost:8080/api/products
```

### Test Cart API
```bash
curl -X POST http://localhost:8080/api/cart/session-123/items \
  -H "Content-Type: application/json" \
  -d '{"productId": 1, "productName": "Test Product", "quantity": 1, "price": 29.99}'
```

## Docker Build

To build Docker images for services:

```bash
cd services/product-catalog-service
docker build -t tcg-shop-product-catalog:1.0.0 .
```

## CI/CD Pipeline

The GitHub Actions pipeline is configured in `.github/workflows/ci-cd.yml`.

It will:
1. Build all services with Maven
2. Run tests
3. Build Docker images
4. Push to GitHub Container Registry
5. Deploy to cloud (configure secrets)

## Next Steps

1. Complete remaining services (Order, Payment, Manufacturing, Review)
2. Add Kafka integration
3. Implement Circuit Breaker in Order Service
4. Add more frontend features
5. Configure production deployment

