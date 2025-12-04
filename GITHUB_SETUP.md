# GitHub Setup & CI/CD Testing Guide

## 🚀 Quick Start

### 1. Push to GitHub

```bash
# Initialize git if not already done
git init

# Add all files
git add .

# Commit
git commit -m "feat: Complete microservices implementation with Kafka and Circuit Breaker"

# Add remote (replace with your repo URL)
git remote add origin https://github.com/YOUR_USERNAME/tcg-accessories-shop.git

# Push to main branch
git push -u origin main
```

### 2. Verify CI/CD Pipeline

1. Go to your GitHub repository
2. Click on the **Actions** tab
3. You should see the workflow running
4. Wait for it to complete

### 3. Check Pipeline Status

The pipeline will:
- ✅ Validate project structure
- ✅ Build all 9 services with Maven
- ✅ Test Docker builds
- ✅ (Optional) Push Docker images to registry

## 📋 Pipeline Stages

### Stage 1: Validate Structure
- Checks that all service directories exist
- Verifies project structure

### Stage 2: Build Services
- Builds each service with Maven
- Runs in parallel using matrix strategy
- Verifies JAR files are created

### Stage 3: Docker Build Test
- Tests Docker build for at least one service
- Verifies Dockerfile works correctly

## 🔧 Configure Secrets (Optional)

If you want to push Docker images to a registry:

1. Go to **Settings** → **Secrets and variables** → **Actions**
2. Add secrets:
   - `GITHUB_TOKEN` (automatically available)
   - `DOCKER_USERNAME` (if using Docker Hub)
   - `DOCKER_PASSWORD` (if using Docker Hub)
   - `FLY_API_TOKEN` (if deploying to Fly.io)

## 🐛 Troubleshooting

### Pipeline Fails on Build

**Check:**
- Maven dependencies are correct
- Java version is 17
- All pom.xml files are valid

**Fix:**
```bash
# Test locally first
cd services/product-catalog-service
mvn clean package -DskipTests
```

### Docker Build Fails

**Check:**
- Dockerfile syntax is correct
- Base images are available
- Multi-stage build works

**Fix:**
```bash
# Test locally
cd services/product-catalog-service
docker build -t test:latest .
```

### Services Not Found

**Check:**
- All service directories exist
- Paths in workflow are correct

## 📊 Monitoring Pipeline

### View Logs
1. Go to Actions tab
2. Click on the workflow run
3. Click on a job to see detailed logs

### Re-run Failed Jobs
1. Click on the failed job
2. Click "Re-run jobs"

## ✅ Success Criteria

Pipeline is successful when:
- ✅ All services build without errors
- ✅ JAR files are created
- ✅ Docker builds work
- ✅ No compilation errors

## 🎯 Next Steps After Successful Pipeline

1. **Deploy to Cloud** (Fly.io, AWS, Azure)
2. **Set up Monitoring** (Prometheus, Grafana)
3. **Add Integration Tests**
4. **Configure Production Environment**

---

**Your CI/CD pipeline is ready! Push to GitHub and watch it work!** 🚀

