# Google Cloud Shell Deployment Guide

This guide is specifically for deploying the TCG Shop microservices stack on **Google Cloud Shell** or similar cloud environments.

## 🔍 Environment Detection

Google Cloud Shell typically:
- Runs as a non-root user but with elevated privileges
- May not have `sudo` installed or configured
- Has Docker pre-installed
- Has limited resources (RAM/CPU)

## ✅ Quick Check

First, verify your environment:

```bash
# Check if Docker is already installed
docker --version
docker compose version

# Check if you're in Cloud Shell
echo $CLOUD_SHELL

# Check available resources
free -h
df -h
```

## 🚀 Deployment Steps

### Option 1: Docker Already Installed (Most Common)

If Docker is already available:

```bash
# Clone repository
git clone <your-repository-url> microservice-arbeit
cd microservice-arbeit

# Navigate to docker directory
cd docker

# Build and start services
docker compose build
docker compose up -d

# Check status
docker compose ps
```

### Option 2: Install Docker Without Sudo

If Docker is not installed and you can't use `sudo`:

```bash
# Check if you can install packages
which apt-get
which yum

# For Cloud Shell, Docker is usually pre-installed
# If not, you may need to use Docker Desktop or request admin access
```

### Option 3: Use Docker Desktop (If Available)

If Docker Desktop is available in your environment:

```bash
# Start Docker Desktop (if GUI available)
# Then proceed with docker compose commands
```

## 🔧 Cloud Shell Specific Considerations

### Resource Limits

Cloud Shell has limited resources. Consider:

1. **Start services selectively**:
```bash
# Start only essential services first
docker compose up -d mysql-product-catalog redis eureka-server api-gateway frontend
```

2. **Reduce resource usage**:
```bash
# Edit docker-compose.yml to add resource limits
# Or use docker-compose.prod.yml with limits
```

3. **Monitor resources**:
```bash
# Check memory usage
docker stats --no-stream

# Check disk space
df -h
```

### Port Forwarding

Cloud Shell uses port forwarding. To access services:

1. **Use Cloud Shell's built-in web preview**:
   - Click the web preview icon in Cloud Shell
   - Select the port (e.g., 3000 for frontend, 8080 for API)

2. **Or use port forwarding**:
```bash
# Forward port 3000 (frontend)
gcloud cloud-shell ssh --ssh-flag="-L 3000:localhost:3000"

# Forward port 8080 (API Gateway)
gcloud cloud-shell ssh --ssh-flag="-L 8080:localhost:8080"
```

## 🐛 Troubleshooting

### Issue: "sudo: command not found"

**Solution**: Cloud Shell often doesn't need `sudo`. Try commands without it:

```bash
# Instead of: sudo apt install
# Try: apt install (if you have permissions)

# Or check if you're already root
whoami
id
```

### Issue: "Permission denied"

**Solution**: Check your permissions:

```bash
# Check current user
whoami
id

# Check Docker permissions
docker ps

# If Docker permission denied, you may need to:
# 1. Request admin access
# 2. Use Docker Desktop
# 3. Contact your administrator
```

### Issue: "Out of memory"

**Solution**: Reduce services or increase Cloud Shell resources:

```bash
# Stop unnecessary services
docker compose stop manufacturing-service review-service

# Or use resource limits in docker-compose.yml
```

### Issue: "Port already in use"

**Solution**: Change ports in docker-compose.yml:

```yaml
services:
  api-gateway:
    ports:
      - "8081:8080"  # Changed from 8080:8080
```

## 📝 Alternative: Use Cloud Run or GKE

For production, consider deploying to:

1. **Cloud Run** (Serverless containers)
2. **GKE** (Google Kubernetes Engine)
3. **Compute Engine** (VM with full control)

See the main deployment guide for production options.

## 🔄 Quick Commands

```bash
# Start all services
cd docker && docker compose up -d

# Stop all services
docker compose down

# View logs
docker compose logs -f

# Check status
docker compose ps

# Restart a service
docker compose restart api-gateway
```

## 📚 Additional Resources

- [Google Cloud Shell Documentation](https://cloud.google.com/shell/docs)
- [Docker on Cloud Shell](https://cloud.google.com/shell/docs/using-cloud-shell#docker)
- [Cloud Shell Limitations](https://cloud.google.com/shell/docs/quotas-limits)


