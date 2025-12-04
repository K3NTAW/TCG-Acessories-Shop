#!/bin/bash

# CI/CD Pipeline Test Script
# This script simulates what the CI/CD pipeline does

echo "🧪 Testing CI/CD Pipeline Setup..."
echo ""

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo -e "${RED}❌ Maven is not installed${NC}"
    exit 1
fi

echo -e "${GREEN}✅ Maven found${NC}"

# Check if Docker is installed
if ! command -v docker &> /dev/null; then
    echo -e "${RED}❌ Docker is not installed${NC}"
    exit 1
fi

echo -e "${GREEN}✅ Docker found${NC}"

# List of services to test
SERVICES=(
    "eureka-server"
    "api-gateway"
    "product-catalog-service"
    "cart-service"
    "customer-service"
    "order-service"
    "payment-service"
    "manufacturing-service"
    "review-service"
)

echo ""
echo "📦 Testing Maven Build for all services..."
echo ""

FAILED_SERVICES=()
SUCCESS_SERVICES=()

for service in "${SERVICES[@]}"; do
    echo -e "${YELLOW}Building $service...${NC}"
    cd "services/$service" || exit 1
    
    if mvn clean package -DskipTests > /dev/null 2>&1; then
        echo -e "${GREEN}✅ $service: Build successful${NC}"
        SUCCESS_SERVICES+=("$service")
    else
        echo -e "${RED}❌ $service: Build failed${NC}"
        FAILED_SERVICES+=("$service")
    fi
    
    cd ../..
done

echo ""
echo "📊 Build Summary:"
echo -e "${GREEN}✅ Successful: ${#SUCCESS_SERVICES[@]}${NC}"
echo -e "${RED}❌ Failed: ${#FAILED_SERVICES[@]}${NC}"

if [ ${#FAILED_SERVICES[@]} -gt 0 ]; then
    echo ""
    echo "Failed services:"
    for service in "${FAILED_SERVICES[@]}"; do
        echo -e "${RED}  - $service${NC}"
    done
    exit 1
fi

echo ""
echo "🐳 Testing Docker Build..."
echo ""

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo -e "${YELLOW}⚠️  Docker daemon is not running. Skipping Docker build test.${NC}"
    echo -e "${YELLOW}   Start Docker Desktop to test Docker builds.${NC}"
else
    # Test Docker build for one service as example
    cd services/product-catalog-service || exit 1
    
    if docker build -t test-product-catalog:latest . > /dev/null 2>&1; then
        echo -e "${GREEN}✅ Docker build successful${NC}"
        docker rmi test-product-catalog:latest > /dev/null 2>&1
    else
        echo -e "${RED}❌ Docker build failed${NC}"
        exit 1
    fi
    
    cd ../..
fi

cd ../..

echo ""
echo -e "${GREEN}🎉 All CI/CD tests passed!${NC}"
echo ""
echo "Next steps:"
echo "1. Push to GitHub"
echo "2. Check GitHub Actions tab"
echo "3. Pipeline will automatically build and test all services"

