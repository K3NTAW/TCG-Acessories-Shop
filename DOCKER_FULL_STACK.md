# 🐳 Full Docker Stack Setup

## Complete Docker Compose Stack

All services are now in Docker! This makes everything:
- ✅ Consistent across all platforms (Mac, Windows, Linux, GitHub Actions)
- ✅ Easy to start with one command
- ✅ Portable and deployable
- ✅ No need for host.docker.internal workarounds

## Quick Start

```bash
cd docker
docker-compose up -d
```

That's it! Everything starts automatically:
- ✅ Databases (MySQL x3, Redis)
- ✅ Kafka + Zookeeper
- ✅ Eureka Server
- ✅ API Gateway
- ✅ All 7 Business Services
- ✅ Frontend

## Service Architecture

```
Frontend (Docker)
    ↓
API Gateway (Docker)
    ↓
Eureka Server (Docker)
    ↓
Business Services (Docker):
  - Product Catalog Service
  - Cart Service
  - Customer Service
  - Order Service
  - Payment Service
  - Manufacturing Service
  - Review Service
    ↓
Databases (Docker):
  - MySQL (x3)
  - Redis
  - Kafka
```

## Service Communication

All services communicate via Docker service names:
- **Eureka**: `eureka-server:8761`
- **API Gateway**: `api-gateway:8080`
- **MySQL**: `mysql-product-catalog:3306`, `mysql-customer:3306`, `mysql-order:3306`
- **Redis**: `redis:6379`
- **Kafka**: `kafka:29092` (internal listener)

## Ports

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

## Starting the Stack

### Full Stack (Recommended)

```bash
cd docker
docker-compose up -d
```

Wait ~60 seconds for all services to start, then:

- **Frontend**: http://localhost:3000
- **Eureka Dashboard**: http://localhost:8761 (check all services are registered)
- **API Gateway**: http://localhost:8080

### Check Status

```bash
docker-compose ps
```

All services should show as "Up" or "Healthy".

### View Logs

```bash
# All services
docker-compose logs -f

# Specific service
docker-compose logs -f product-catalog-service
docker-compose logs -f api-gateway
```

## Stopping the Stack

```bash
cd docker
docker-compose down
```

To remove volumes (clean database):
```bash
docker-compose down -v
```

## Rebuilding Services

After code changes:

```bash
# Rebuild specific service
docker-compose build product-catalog-service
docker-compose up -d product-catalog-service

# Rebuild all services
docker-compose build
docker-compose up -d
```

## Environment Variables

Services use environment variables for configuration:

- **Database URLs**: Set via `SPRING_DATASOURCE_URL`
- **Redis**: Set via `SPRING_DATA_REDIS_HOST` and `SPRING_DATA_REDIS_PORT`
- **Kafka**: Set via `SPRING_KAFKA_BOOTSTRAP_SERVERS`
- **Eureka**: Set via `EUREKA_CLIENT_SERVICEURL_DEFAULTZONE`

Defaults are in `application.yml` for local development, Docker overrides them.

## Troubleshooting

### Services Not Starting

1. **Check logs**:
   ```bash
   docker-compose logs service-name
   ```

2. **Check health**:
   ```bash
   docker-compose ps
   ```

3. **Restart service**:
   ```bash
   docker-compose restart service-name
   ```

### Services Not Registering with Eureka

1. **Check Eureka is running**: http://localhost:8761
2. **Check service logs** for registration errors
3. **Verify network**: All services must be on `microservices-network`

### Database Connection Issues

- Services wait for database health checks before starting
- Check database logs: `docker-compose logs mysql-product-catalog`

### Kafka Connection Issues

- Services use `kafka:29092` (internal listener) in Docker
- Check Kafka logs: `docker-compose logs kafka`

## Benefits of Full Docker Stack

1. **One Command Start**: `docker-compose up -d`
2. **Consistent Environment**: Same on Mac, Windows, Linux, CI/CD
3. **Easy Scaling**: Can scale individual services
4. **Isolation**: Each service in its own container
5. **Portability**: Works everywhere Docker runs
6. **GitHub Actions Ready**: Same setup works in CI/CD

## Development Workflow

### Option 1: Full Docker (Current)

```bash
cd docker
docker-compose up -d
# Everything runs in Docker
```

### Option 2: Hybrid (For Development)

If you want hot reload for services:

1. Start infrastructure in Docker:
   ```bash
   cd docker
   docker-compose up -d mysql-product-catalog mysql-customer mysql-order redis kafka zookeeper eureka-server
   ```

2. Run services locally:
   ```bash
   cd services/product-catalog-service
   mvn spring-boot:run
   ```

3. Run frontend locally:
   ```bash
   cd frontend/tcg-shop-frontend
   npm run dev
   ```

## Next Steps

- ✅ All services in Docker
- ✅ Environment-based configuration
- ✅ Health checks and dependencies
- ✅ Works on all platforms

You can now deploy this entire stack anywhere Docker runs! 🚀

