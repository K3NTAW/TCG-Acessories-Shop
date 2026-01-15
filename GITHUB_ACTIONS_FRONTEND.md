# 🚀 Frontend Configuration for GitHub Actions

## The Problem

The `host.docker.internal` approach works on **Mac and Windows** Docker Desktop, but **NOT on Linux** (which GitHub Actions uses).

## Solution: Environment Variable Configuration

The frontend Docker image now uses an **environment variable** `API_GATEWAY_URL` that can be set differently for different environments.

## How It Works

1. **Local (Mac/Windows)**: Uses `host.docker.internal:8080` (default)
2. **GitHub Actions / Linux**: Set `API_GATEWAY_URL` environment variable
3. **Docker Compose with API Gateway**: Use service name `api-gateway:8080`

## Configuration

### Current Setup (Local Mac/Windows)

In `docker/docker-compose.yml`:
```yaml
frontend:
  environment:
    - API_GATEWAY_URL=http://host.docker.internal:8080  # Works on Mac/Windows
```

### For GitHub Actions

**Option 1: If API Gateway is in Docker Compose**

Add API Gateway to your docker-compose and use service name:

```yaml
services:
  api-gateway:
    # ... your API Gateway config
    networks:
      - microservices-network

  frontend:
    environment:
      - API_GATEWAY_URL=http://api-gateway:8080  # Use service name
    networks:
      - microservices-network
```

**Option 2: Set Environment Variable in GitHub Actions**

```yaml
jobs:
  test-frontend:
    runs-on: ubuntu-latest
    steps:
      - name: Build and run frontend
        env:
          API_GATEWAY_URL: http://localhost:8080  # If API Gateway is on host
        run: |
          docker build -t frontend .
          docker run -e API_GATEWAY_URL=$API_GATEWAY_URL -p 3000:80 frontend
```

**Option 3: Full Docker Compose in GitHub Actions**

```yaml
jobs:
  test-full-stack:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      
      - name: Start all services
        run: |
          cd docker
          # Make sure API_GATEWAY_URL is set for Linux
          # Either in docker-compose.yml or via environment
          API_GATEWAY_URL=http://api-gateway:8080 docker-compose up -d
```

## Quick Fix for GitHub Actions

The easiest solution is to **add API Gateway to docker-compose.yml**:

1. Add API Gateway service to `docker/docker-compose.yml`
2. Update frontend environment:
   ```yaml
   frontend:
     environment:
       - API_GATEWAY_URL=http://api-gateway:8080  # Change this
   ```

This works everywhere: Mac, Windows, Linux, GitHub Actions! ✅

## Testing

To test locally on Linux (or simulate GitHub Actions):

```bash
# Set environment variable
export API_GATEWAY_URL=http://api-gateway:8080

# Or in docker-compose.yml, override:
docker-compose up -d --env API_GATEWAY_URL=http://api-gateway:8080
```

## Summary

- ✅ **Local Mac/Windows**: `API_GATEWAY_URL=http://host.docker.internal:8080` (current)
- ✅ **GitHub Actions / Linux**: `API_GATEWAY_URL=http://api-gateway:8080` (if in Docker)
- ✅ **GitHub Actions / Host**: `API_GATEWAY_URL=http://localhost:8080` (if on host)

The frontend will automatically use whatever `API_GATEWAY_URL` is set! 🚀
