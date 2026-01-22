# 🔧 Docker + Host Services Setup

## Current Architecture

- **In Docker**: Eureka Server, API Gateway, Frontend, Databases, Redis, Kafka
- **On Host**: Business Services (Product Catalog, Cart, Customer, Order, Payment, Manufacturing, Review)

## Why This Setup?

This hybrid approach allows:
- ✅ Quick development (services run locally with hot reload)
- ✅ Infrastructure in Docker (databases, message broker)
- ✅ Frontend in Docker (consistent deployment)

## How It Works

### Service Registration

Services running on the host register with Eureka using `hostname: host.docker.internal`. This allows:

1. **Services on host** → Register with Eureka in Docker via `localhost:8761`
2. **API Gateway in Docker** → Discovers services via Eureka
3. **API Gateway** → Calls services at `host.docker.internal:8081` (etc.)

### Port Mapping

| Service | Host Port | Docker Access |
|---------|-----------|---------------|
| Product Catalog | 8081 | `host.docker.internal:8081` |
| Cart | 8082 | `host.docker.internal:8082` |
| Customer | 8083 | `host.docker.internal:8083` |
| Order | 8084 | `host.docker.internal:8084` |
| Payment | 8085 | `host.docker.internal:8085` |
| Manufacturing | 8086 | `host.docker.internal:8086` |
| Review | 8087 | `host.docker.internal:8087` |

## Starting the System

### Step 1: Start Docker Services

```bash
cd docker
docker-compose up -d
```

This starts:
- Eureka Server (port 8761)
- API Gateway (port 8080)
- Frontend (port 3000)
- MySQL, Redis, Kafka

### Step 2: Start Business Services on Host

Start each service in a separate terminal:

```bash
# Terminal 1: Product Catalog
cd services/product-catalog-service
mvn spring-boot:run

# Terminal 2: Cart
cd services/cart-service
mvn spring-boot:run

# Terminal 3: Customer
cd services/customer-service
mvn spring-boot:run

# Terminal 4: Order
cd services/order-service
mvn spring-boot:run

# Terminal 5: Payment
cd services/payment-service
mvn spring-boot:run

# Terminal 6: Manufacturing
cd services/manufacturing-service
mvn spring-boot:run

# Terminal 7: Review
cd services/review-service
mvn spring-boot:run
```

### Step 3: Verify

1. **Check Eureka**: http://localhost:8761
   - You should see all services registered

2. **Test API Gateway**: 
   ```bash
   curl http://localhost:8080/api/products
   ```

3. **Test Frontend**: http://localhost:3000

## Troubleshooting

### Services Not Appearing in Eureka

- Check services are running: `lsof -i :8081` (for product-catalog)
- Check Eureka is accessible: `curl http://localhost:8761`
- Check service logs for registration errors

### API Gateway Returns 500

- Check if service is registered in Eureka
- Check API Gateway logs: `docker-compose logs api-gateway`
- Verify service is accessible: `curl http://host.docker.internal:8081/products`

### CORS Errors

- Frontend now uses `/api` (relative URL) which goes through nginx proxy
- Nginx proxies to `api-gateway:8080` in Docker
- Make sure frontend container is rebuilt with latest changes

## Alternative: All Services in Docker

If you want everything in Docker (better for CI/CD), you can add all services to `docker-compose.yml`. This is more complex but more portable.

