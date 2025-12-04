# Frontend Docker Setup

## ✅ Frontend CAN Run in Docker

The frontend has a Dockerfile and can be added to docker-compose.yml.

## 🏗️ How It Works

### Development Mode (Current)
- Run with `npm run dev` (Vite dev server)
- Hot reload, fast development
- Port: 3000
- **Not in Docker** - runs directly on your machine

### Production Mode (Docker)
- Builds React app into static files
- Serves with Nginx (web server)
- Port: 3000 (mapped to container port 80)
- **In Docker** - fully containerized

## 📦 Docker Setup

The frontend Dockerfile:
1. **Build Stage**: Compiles React app with Vite
2. **Production Stage**: Serves static files with Nginx

### To Run Frontend in Docker:

```bash
# Build and start (from docker directory)
cd docker
docker-compose up -d frontend
```

Or build manually:
```bash
cd frontend/tcg-shop-frontend
docker build -t tcg-shop-frontend:latest .
docker run -p 3000:80 tcg-shop-frontend:latest
```

## 🔄 Current Setup

**Currently in docker-compose.yml:**
- ✅ Infrastructure services (MySQL, Redis, Kafka, Zookeeper)
- ❌ Frontend (not included yet)
- ❌ Microservices (run separately with Maven)

**Why?**
- For **development**, it's easier to run frontend with `npm run dev` (hot reload)
- For **production**, frontend should be in Docker

## 🎯 Options

### Option 1: Development (Current)
- Frontend: `npm run dev` (not in Docker)
- Backend services: `mvn spring-boot:run` (not in Docker)
- Infrastructure: Docker Compose

### Option 2: Full Docker (Production-like)
- Frontend: Docker container
- Backend services: Docker containers
- Infrastructure: Docker Compose

## 💡 Recommendation

**For Development:**
- Keep frontend out of Docker (easier debugging, hot reload)
- Use `npm run dev` for fast iteration

**For Production/CI/CD:**
- Use Docker for frontend (consistent deployment)
- Build once, deploy anywhere

---

**The frontend CAN run in Docker, but for development it's usually better to run it directly with npm.**

