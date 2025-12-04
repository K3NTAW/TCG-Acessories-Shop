#!/bin/bash

# Startup Script for TCG Accessories Shop
# This script helps you start all services

echo "🚀 Starting TCG Accessories Shop..."
echo ""

# Colors
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo -e "${YELLOW}⚠️  Docker is not running. Please start Docker Desktop first.${NC}"
    exit 1
fi

echo -e "${GREEN}✅ Docker is running${NC}"

# Start Docker services
echo ""
echo "📦 Starting Docker services (MySQL, Redis, Kafka)..."
cd docker
docker-compose up -d
cd ..

echo ""
echo -e "${GREEN}✅ Docker services started${NC}"
echo ""
echo "⏳ Waiting 30 seconds for services to be ready..."
sleep 30

echo ""
echo "📋 Next steps:"
echo ""
echo "1. Open multiple terminal windows (9 terminals)"
echo ""
echo "2. Start services in this order:"
echo "   Terminal 1: cd services/eureka-server && mvn spring-boot:run"
echo "   Terminal 2: cd services/api-gateway && mvn spring-boot:run"
echo "   Terminal 3: cd services/product-catalog-service && mvn spring-boot:run"
echo "   Terminal 4: cd services/cart-service && mvn spring-boot:run"
echo "   Terminal 5: cd services/customer-service && mvn spring-boot:run"
echo "   Terminal 6: cd services/order-service && mvn spring-boot:run"
echo "   Terminal 7: cd services/payment-service && mvn spring-boot:run"
echo "   Terminal 8: cd services/manufacturing-service && mvn spring-boot:run"
echo "   Terminal 9: cd services/review-service && mvn spring-boot:run"
echo ""
echo "3. Start Frontend:"
echo "   cd frontend/tcg-shop-frontend && npm install && npm run dev"
echo ""
echo "4. Open browser: http://localhost:3000"
echo ""
echo "📊 Check Eureka Dashboard: http://localhost:8761"
echo ""

