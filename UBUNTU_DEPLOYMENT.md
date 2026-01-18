# Ubuntu Deployment Guide

Complete guide for hosting the TCG Shop microservices stack on Ubuntu.

## 📋 Prerequisites

### System Requirements
- **OS**: Ubuntu 20.04 LTS or later (22.04 LTS recommended)
- **RAM**: Minimum 4GB (8GB+ recommended for all services)
- **CPU**: 2+ cores
- **Disk**: 20GB+ free space
- **Network**: Internet connection for initial setup

### Required Software
- Docker Engine 20.10+
- Docker Compose 2.0+
- Git

## 🚀 Installation Steps

### 1. Update System

```bash
sudo apt update && sudo apt upgrade -y
```

### 2. Install Docker

```bash
# Remove old versions
sudo apt remove docker docker-engine docker.io containerd runc

# Install prerequisites
sudo apt install -y \
    ca-certificates \
    curl \
    gnupg \
    lsb-release

# Add Docker's official GPG key
sudo mkdir -p /etc/apt/keyrings
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /etc/apt/keyrings/docker.gpg

# Set up repository
echo \
  "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/ubuntu \
  $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null

# Install Docker Engine
sudo apt update
sudo apt install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin

# Add your user to docker group (to run without sudo)
sudo usermod -aG docker $USER

# Log out and back in, or run:
newgrp docker

# Verify installation
docker --version
docker compose version
```

### 3. Clone Repository

```bash
# Navigate to your desired directory
cd ~
git clone <your-repository-url> microservice-arbeit
cd microservice-arbeit
```

### 4. Configure Environment (Optional)

Create a `.env` file in the `docker/` directory if you need to customize settings:

```bash
cd docker
cat > .env << EOF
# Database Passwords
MYSQL_ROOT_PASSWORD=your_secure_root_password
MYSQL_PASSWORD=your_secure_app_password

# API Gateway Port (default: 8080)
API_GATEWAY_PORT=8080

# Frontend Port (default: 3000)
FRONTEND_PORT=3000

# Kafka Configuration
KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://your-server-ip:9092,PLAINTEXT_INTERNAL://kafka:29092
EOF
```

**Note**: Update `KAFKA_ADVERTISED_LISTENERS` with your server's IP address if accessing from external machines.

### 5. Build and Start Services

```bash
# Navigate to docker directory
cd docker

# Build all images (this may take 10-15 minutes)
docker compose build

# Start all services
docker compose up -d

# View logs
docker compose logs -f

# Check service status
docker compose ps
```

### 6. Verify Services

```bash
# Check if all containers are running
docker compose ps

# Check service health
curl http://localhost:8761  # Eureka Server
curl http://localhost:8080/api/products  # API Gateway
curl http://localhost:3000  # Frontend

# View logs for specific service
docker compose logs -f api-gateway
docker compose logs -f product-catalog-service
```

## 🔧 Service Management

### Start All Services
```bash
cd docker
docker compose up -d
```

### Stop All Services
```bash
cd docker
docker compose down
```

### Stop and Remove Volumes (⚠️ Deletes Data)
```bash
cd docker
docker compose down -v
```

### Restart Specific Service
```bash
cd docker
docker compose restart api-gateway
```

### View Logs
```bash
# All services
docker compose logs -f

# Specific service
docker compose logs -f product-catalog-service

# Last 100 lines
docker compose logs --tail=100 api-gateway
```

### Rebuild and Restart
```bash
cd docker
docker compose build --no-cache api-gateway
docker compose up -d api-gateway
```

## 🌐 Network Configuration

### Ports Used

| Service | Port | Description |
|---------|------|-------------|
| Frontend | 3000 | Web UI |
| API Gateway | 8080 | Main API endpoint |
| Eureka Server | 8761 | Service discovery |
| Product Catalog | 8081 | Product service |
| Cart Service | 8082 | Cart service |
| Customer Service | 8083 | Customer service |
| Order Service | 8084 | Order service |
| Payment Service | 8085 | Payment service |
| Manufacturing | 8086 | Manufacturing service |
| Review Service | 8087 | Review service |
| MySQL (Product) | 3306 | Product database |
| MySQL (Customer) | 3307 | Customer database |
| MySQL (Order) | 3308 | Order database |
| Redis | 6379 | Cart cache |
| Kafka | 9092 | Message broker |
| Zookeeper | 2181 | Kafka coordination |

### Firewall Configuration

```bash
# Install UFW if not present
sudo apt install -y ufw

# Allow SSH (important!)
sudo ufw allow 22/tcp

# Allow HTTP/HTTPS
sudo ufw allow 80/tcp
sudo ufw allow 443/tcp

# Allow application ports
sudo ufw allow 3000/tcp  # Frontend
sudo ufw allow 8080/tcp   # API Gateway
sudo ufw allow 8761/tcp   # Eureka (optional, for monitoring)

# Enable firewall
sudo ufw enable

# Check status
sudo ufw status
```

## 🔒 Production Considerations

### 1. Reverse Proxy (Nginx)

Install and configure Nginx as a reverse proxy:

```bash
# Install Nginx
sudo apt install -y nginx

# Create configuration
sudo nano /etc/nginx/sites-available/tcg-shop
```

Add this configuration:

```nginx
server {
    listen 80;
    server_name your-domain.com;

    # Frontend
    location / {
        proxy_pass http://localhost:3000;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection 'upgrade';
        proxy_set_header Host $host;
        proxy_cache_bypass $http_upgrade;
    }

    # API Gateway
    location /api {
        proxy_pass http://localhost:8080;
        proxy_http_version 1.1;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    # Eureka Dashboard (optional, restrict access)
    location /eureka {
        proxy_pass http://localhost:8761;
        allow 127.0.0.1;
        deny all;
    }
}
```

Enable the site:

```bash
sudo ln -s /etc/nginx/sites-available/tcg-shop /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl restart nginx
```

### 2. SSL Certificate (Let's Encrypt)

```bash
# Install Certbot
sudo apt install -y certbot python3-certbot-nginx

# Get certificate
sudo certbot --nginx -d your-domain.com

# Auto-renewal is set up automatically
```

### 3. Systemd Service (Auto-start)

Create a systemd service to auto-start Docker Compose:

```bash
sudo nano /etc/systemd/system/tcg-shop.service
```

Add:

```ini
[Unit]
Description=TCG Shop Microservices
Requires=docker.service
After=docker.service

[Service]
Type=oneshot
RemainAfterExit=yes
WorkingDirectory=/home/your-user/microservice-arbeit/docker
ExecStart=/usr/bin/docker compose up -d
ExecStop=/usr/bin/docker compose down
TimeoutStartSec=0

[Install]
WantedBy=multi-user.target
```

Enable the service:

```bash
sudo systemctl daemon-reload
sudo systemctl enable tcg-shop.service
sudo systemctl start tcg-shop.service
```

### 4. Resource Limits

Update `docker-compose.yml` to add resource limits:

```yaml
services:
  api-gateway:
    deploy:
      resources:
        limits:
          cpus: '1'
          memory: 512M
        reservations:
          cpus: '0.5'
          memory: 256M
```

### 5. Log Rotation

Create log rotation configuration:

```bash
sudo nano /etc/logrotate.d/docker-containers
```

Add:

```
/var/lib/docker/containers/*/*.log {
    rotate 7
    daily
    compress
    size=10M
    missingok
    delaycompress
    copytruncate
}
```

## 📊 Monitoring

### Docker Stats

```bash
# Real-time stats
docker stats

# Specific service
docker stats api-gateway
```

### Health Checks

```bash
# Eureka Dashboard
curl http://localhost:8761

# Service health endpoints
curl http://localhost:8080/actuator/health
curl http://localhost:8081/actuator/health
```

### Log Monitoring

```bash
# Follow all logs
docker compose logs -f

# Filter for errors
docker compose logs | grep -i error

# Service-specific errors
docker compose logs api-gateway | grep -i error
```

## 🐛 Troubleshooting

### Services Not Starting

```bash
# Check container status
docker compose ps

# Check logs
docker compose logs service-name

# Check if ports are in use
sudo netstat -tulpn | grep :8080
```

### Database Connection Issues

```bash
# Check MySQL containers
docker compose ps mysql-product-catalog
docker compose logs mysql-product-catalog

# Connect to MySQL
docker compose exec mysql-product-catalog mysql -u appuser -p
```

### Out of Memory

```bash
# Check memory usage
free -h
docker stats

# Increase swap (if needed)
sudo fallocate -l 2G /swapfile
sudo chmod 600 /swapfile
sudo mkswap /swapfile
sudo swapon /swapfile
```

### Rebuild Everything

```bash
cd docker
docker compose down -v
docker compose build --no-cache
docker compose up -d
```

## 🔄 Backup and Restore

### Backup Databases

```bash
# Create backup directory
mkdir -p ~/backups

# Backup MySQL databases
docker compose exec mysql-product-catalog mysqldump -u appuser -papppassword product_catalog_db > ~/backups/product_catalog_$(date +%Y%m%d).sql
docker compose exec mysql-customer mysqldump -u appuser -papppassword customer_db > ~/backups/customer_$(date +%Y%m%d).sql
docker compose exec mysql-order mysqldump -u appuser -papppassword order_db > ~/backups/order_$(date +%Y%m%d).sql

# Backup Redis
docker compose exec redis redis-cli SAVE
docker compose cp redis:/data/dump.rdb ~/backups/redis_$(date +%Y%m%d).rdb
```

### Restore Databases

```bash
# Restore MySQL
docker compose exec -T mysql-product-catalog mysql -u appuser -papppassword product_catalog_db < ~/backups/product_catalog_YYYYMMDD.sql

# Restore Redis
docker compose cp ~/backups/redis_YYYYMMDD.rdb redis:/data/dump.rdb
docker compose restart redis
```

## 📝 Quick Reference

```bash
# Start everything
cd docker && docker compose up -d

# Stop everything
cd docker && docker compose down

# View logs
docker compose logs -f

# Restart service
docker compose restart service-name

# Rebuild service
docker compose build service-name && docker compose up -d service-name

# Check status
docker compose ps

# Access service shell
docker compose exec service-name sh

# View resource usage
docker stats
```

## 🎯 Access Points

After deployment, access:

- **Frontend**: http://your-server-ip:3000
- **API Gateway**: http://your-server-ip:8080/api
- **Eureka Dashboard**: http://your-server-ip:8761
- **Product Catalog API**: http://your-server-ip:8080/api/products
- **Cart API**: http://your-server-ip:8080/api/cart

## 📚 Additional Resources

- [Docker Documentation](https://docs.docker.com/)
- [Docker Compose Documentation](https://docs.docker.com/compose/)
- [Nginx Documentation](https://nginx.org/en/docs/)
- [Let's Encrypt Documentation](https://letsencrypt.org/docs/)

