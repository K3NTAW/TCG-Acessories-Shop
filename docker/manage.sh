#!/bin/bash

# TCG Shop - Docker Stack Management Script

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Functions
print_header() {
    echo -e "${BLUE}================================${NC}"
    echo -e "${BLUE}$1${NC}"
    echo -e "${BLUE}================================${NC}"
}

print_success() {
    echo -e "${GREEN}✅ $1${NC}"
}

print_error() {
    echo -e "${RED}❌ $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}⚠️  $1${NC}"
}

# Check if docker compose is available
if ! command -v docker &> /dev/null; then
    print_error "Docker is not installed. Please install Docker first."
    exit 1
fi

# Parse command
case "$1" in
    start)
        print_header "Starting TCG Shop Services"
        docker compose up -d
        print_success "Services started"
        echo ""
        echo "Waiting for services to be healthy..."
        sleep 10
        docker compose ps
        echo ""
        print_success "Access points:"
        echo "  Frontend: http://localhost:3000"
        echo "  API Gateway: http://localhost:8080/api"
        echo "  Eureka: http://localhost:8761"
        ;;
    
    stop)
        print_header "Stopping TCG Shop Services"
        docker compose down
        print_success "Services stopped"
        ;;
    
    restart)
        print_header "Restarting TCG Shop Services"
        docker compose restart
        print_success "Services restarted"
        ;;
    
    status)
        print_header "Service Status"
        docker compose ps
        echo ""
        print_header "Resource Usage"
        docker stats --no-stream
        ;;
    
    logs)
        if [ -z "$2" ]; then
            print_header "All Service Logs"
            docker compose logs -f
        else
            print_header "Logs for $2"
            docker compose logs -f "$2"
        fi
        ;;
    
    build)
        print_header "Building Docker Images"
        if [ -z "$2" ]; then
            docker compose build
        else
            docker compose build "$2"
        fi
        print_success "Build complete"
        ;;
    
    rebuild)
        print_header "Rebuilding Docker Images"
        if [ -z "$2" ]; then
            docker compose build --no-cache
            docker compose up -d
        else
            docker compose build --no-cache "$2"
            docker compose up -d "$2"
        fi
        print_success "Rebuild complete"
        ;;
    
    clean)
        print_warning "This will remove all containers, volumes, and data!"
        read -p "Are you sure? (yes/no): " confirm
        if [ "$confirm" = "yes" ]; then
            print_header "Cleaning Up"
            docker compose down -v
            docker system prune -f
            print_success "Cleanup complete"
        else
            print_warning "Cleanup cancelled"
        fi
        ;;
    
    health)
        print_header "Health Checks"
        echo "Checking Eureka Server..."
        if curl -s http://localhost:8761 > /dev/null; then
            print_success "Eureka Server: OK"
        else
            print_error "Eureka Server: DOWN"
        fi
        
        echo "Checking API Gateway..."
        if curl -s http://localhost:8080/actuator/health > /dev/null; then
            print_success "API Gateway: OK"
        else
            print_error "API Gateway: DOWN"
        fi
        
        echo "Checking Frontend..."
        if curl -s http://localhost:3000 > /dev/null; then
            print_success "Frontend: OK"
        else
            print_error "Frontend: DOWN"
        fi
        ;;
    
    backup)
        print_header "Backing Up Databases"
        BACKUP_DIR="$HOME/backups/tcg-shop-$(date +%Y%m%d-%H%M%S)"
        mkdir -p "$BACKUP_DIR"
        
        echo "Backing up MySQL databases..."
        docker compose exec -T mysql-product-catalog mysqldump -u appuser -papppassword product_catalog_db > "$BACKUP_DIR/product_catalog.sql" 2>/dev/null || print_warning "Product catalog backup failed"
        docker compose exec -T mysql-customer mysqldump -u appuser -papppassword customer_db > "$BACKUP_DIR/customer.sql" 2>/dev/null || print_warning "Customer backup failed"
        docker compose exec -T mysql-order mysqldump -u appuser -papppassword order_db > "$BACKUP_DIR/order.sql" 2>/dev/null || print_warning "Order backup failed"
        
        echo "Backing up Redis..."
        docker compose exec redis redis-cli SAVE > /dev/null 2>&1
        docker compose cp redis:/data/dump.rdb "$BACKUP_DIR/redis.rdb" 2>/dev/null || print_warning "Redis backup failed"
        
        print_success "Backup saved to: $BACKUP_DIR"
        ;;
    
    *)
        echo "TCG Shop - Docker Stack Management"
        echo ""
        echo "Usage: $0 {start|stop|restart|status|logs|build|rebuild|clean|health|backup}"
        echo ""
        echo "Commands:"
        echo "  start [service]    - Start all services (or specific service)"
        echo "  stop               - Stop all services"
        echo "  restart [service]  - Restart all services (or specific service)"
        echo "  status             - Show service status and resource usage"
        echo "  logs [service]     - Show logs (all services or specific)"
        echo "  build [service]    - Build Docker images"
        echo "  rebuild [service]  - Rebuild Docker images (no cache)"
        echo "  clean              - Remove all containers and volumes (⚠️  deletes data)"
        echo "  health             - Check service health"
        echo "  backup             - Backup databases"
        echo ""
        echo "Examples:"
        echo "  $0 start                    # Start all services"
        echo "  $0 logs api-gateway         # View API Gateway logs"
        echo "  $0 rebuild product-catalog-service  # Rebuild specific service"
        exit 1
        ;;
esac


