# Quick Start Guide - Ubuntu Deployment

## 🚀 Fast Setup (5 minutes)

### Option 1: Automated Setup Script

```bash
# Clone repository
git clone <your-repo-url> microservice-arbeit
cd microservice-arbeit

# Run setup script
./setup-ubuntu.sh

# Log out and back in (or run: newgrp docker)

# Start services
cd docker
docker compose build
docker compose up -d
```

### Option 2: Manual Setup

```bash
# 1. Install Docker
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh
sudo usermod -aG docker $USER
newgrp docker

# 2. Clone and start
git clone <your-repo-url> microservice-arbeit
cd microservice-arbeit/docker
docker compose build
docker compose up -d
```

## ✅ Verify Installation

```bash
# Check all services are running
docker compose ps

# Check logs
docker compose logs -f

# Test endpoints
curl http://localhost:3000          # Frontend
curl http://localhost:8080/api/products  # API
curl http://localhost:8761          # Eureka
```

## 🌐 Access Points

- **Frontend**: http://localhost:3000
- **API Gateway**: http://localhost:8080/api
- **Eureka Dashboard**: http://localhost:8761

## 📚 Full Documentation

See [UBUNTU_DEPLOYMENT.md](./UBUNTU_DEPLOYMENT.md) for complete setup, production configuration, monitoring, and troubleshooting.

## 🔧 Common Commands

```bash
# Start
docker compose up -d

# Stop
docker compose down

# Restart
docker compose restart

# View logs
docker compose logs -f [service-name]

# Rebuild
docker compose build [service-name]
docker compose up -d [service-name]
```

