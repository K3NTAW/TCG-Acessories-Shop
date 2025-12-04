# 🔧 Database Connection Fix

## Problem
Services were trying to connect to Docker hostnames (`mysql-customer`, `mysql-order`, etc.) which only work inside Docker containers.

## Solution
Updated all `application.yml` files to use `localhost` with the mapped ports for local development:

- **Product Catalog**: `localhost:3306` (MySQL port mapping)
- **Customer**: `localhost:3307` (MySQL port mapping)
- **Order**: `localhost:3308` (MySQL port mapping)
- **Cart**: `localhost:6379` (Redis port mapping)
- **Kafka**: `localhost:9092` (Kafka port mapping)

## Important: Start Docker First!

**Before starting any services, you MUST start Docker:**

```bash
cd docker
docker-compose up -d
```

Wait ~30 seconds for all databases to be ready, then start your services.

## Port Mappings Reference

| Service | Docker Hostname | Localhost Port |
|---------|----------------|----------------|
| MySQL Product Catalog | mysql-product-catalog:3306 | localhost:3306 |
| MySQL Customer | mysql-customer:3306 | localhost:3307 |
| MySQL Order | mysql-order:3306 | localhost:3308 |
| Redis | redis:6379 | localhost:6379 |
| Kafka | kafka:29092 | localhost:9092 |

## Now You Can Start Services

After Docker is running, start services in order:

1. Eureka Server
2. API Gateway
3. Other services...

All services will now connect to databases on localhost! ✅

