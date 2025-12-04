# 🚀 How to Start the Application

## Quick Start Guide

### Step 1: Start Infrastructure Services

```bash
cd docker
docker-compose up -d
```

This starts:
- MySQL databases (3 instances)
- Redis
- Kafka + Zookeeper

**Wait ~30 seconds** for all services to be ready.

### Step 2: Start Backend Services

Open **multiple terminal windows** (one for each service):

#### Terminal 1: Eureka Server (MUST START FIRST)
```bash
cd services/eureka-server
mvn spring-boot:run
```
Wait until you see: "Started EurekaServerApplication"

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

#### Terminal 6: Order Service
```bash
cd services/order-service
mvn spring-boot:run
```

#### Terminal 7: Payment Service
```bash
cd services/payment-service
mvn spring-boot:run
```

#### Terminal 8: Manufacturing Service
```bash
cd services/manufacturing-service
mvn spring-boot:run
```

#### Terminal 9: Review Service
```bash
cd services/review-service
mvn spring-boot:run
```

### Step 3: Start Frontend

```bash
cd frontend/tcg-shop-frontend
npm install  # Only needed first time
npm run dev
```

### Step 4: Access the Application

- **Frontend**: http://localhost:3000
- **API Gateway**: http://localhost:8080
- **Eureka Dashboard**: http://localhost:8761
- **Swagger UI** (any service): http://localhost:{port}/swagger-ui.html

## 🎯 Service Ports

| Service | Port | URL |
|---------|------|-----|
| Frontend | 3000 | http://localhost:3000 |
| API Gateway | 8080 | http://localhost:8080 |
| Eureka | 8761 | http://localhost:8761 |
| Product Catalog | 8081 | http://localhost:8081 |
| Cart | 8082 | http://localhost:8082 |
| Customer | 8083 | http://localhost:8083 |
| Order | 8084 | http://localhost:8084 |
| Payment | 8085 | http://localhost:8085 |
| Manufacturing | 8086 | http://localhost:8086 |
| Review | 8087 | http://localhost:8087 |

## ⚡ Quick Test

Once everything is running, test the API:

```bash
# Get all products
curl http://localhost:8080/api/products

# Check Eureka (see all registered services)
open http://localhost:8761
```

## 🐛 Troubleshooting

### Services won't start?
1. Make sure Docker is running: `docker ps`
2. Check if ports are already in use
3. Start Eureka Server FIRST, wait 10 seconds, then start others

### Frontend won't start?
1. Make sure Node.js is installed: `node --version` (should be 18+)
2. Run `npm install` in frontend directory
3. Check if port 3000 is available

### Can't connect to services?
1. Check Eureka dashboard - all services should be registered
2. Verify API Gateway is running (port 8080)
3. Check service logs for errors

## 📝 Alternative: Start Script (Coming Soon)

You can create a startup script to start all services at once, but for now, use separate terminals to see individual service logs.

