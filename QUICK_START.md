# ⚡ Quick Start - View the Frontend

## 🎯 Fastest Way to See the Frontend

### Option 1: Just Frontend (for UI testing)

If you just want to see the frontend UI (it will show errors without backend, but you can see the design):

```bash
cd frontend/tcg-shop-frontend
npm install
npm run dev
```

Then open: **http://localhost:3000**

### Option 2: Full Stack (Recommended)

#### 1️⃣ Start Infrastructure (One Command)

```bash
cd docker
docker-compose up -d
```

Wait 30 seconds for services to start.

#### 2️⃣ Start Backend Services

**You need 9 terminal windows.** Here's the order:

**Terminal 1** - Eureka (START THIS FIRST):
```bash
cd services/eureka-server
mvn spring-boot:run
```
⏳ Wait until you see "Started EurekaServerApplication"

**Terminal 2** - API Gateway:
```bash
cd services/api-gateway
mvn spring-boot:run
```

**Terminal 3** - Product Catalog:
```bash
cd services/product-catalog-service
mvn spring-boot:run
```

**Terminal 4** - Cart:
```bash
cd services/cart-service
mvn spring-boot:run
```

**Terminal 5** - Customer:
```bash
cd services/customer-service
mvn spring-boot:run
```

**Terminal 6** - Order:
```bash
cd services/order-service
mvn spring-boot:run
```

**Terminal 7** - Payment:
```bash
cd services/payment-service
mvn spring-boot:run
```

**Terminal 8** - Manufacturing:
```bash
cd services/manufacturing-service
mvn spring-boot:run
```

**Terminal 9** - Review:
```bash
cd services/review-service
mvn spring-boot:run
```

#### 3️⃣ Start Frontend

**Terminal 10** (or new terminal):
```bash
cd frontend/tcg-shop-frontend
npm install  # Only first time
npm run dev
```

#### 4️⃣ Open Browser

🌐 **Frontend**: http://localhost:3000

📊 **Eureka Dashboard** (see all services): http://localhost:8761

## 🎨 What You'll See

- **Home Page**: Welcome page with product categories
- **Products Page**: Browse all products
- **Product Details**: View individual products
- **Shopping Cart**: Add/remove items
- **Login/Register**: User authentication

## ⚠️ Important Notes

1. **Start Eureka FIRST** - Other services need it to register
2. **Wait between starts** - Give each service 5-10 seconds to start
3. **Check Eureka Dashboard** - All services should appear at http://localhost:8761
4. **Frontend connects to API Gateway** - Make sure API Gateway (port 8080) is running

## 🐛 Troubleshooting

**Frontend shows errors?**
- Make sure API Gateway is running (port 8080)
- Check browser console for specific errors
- Verify services are registered in Eureka

**Services won't start?**
- Check if Docker is running: `docker ps`
- Make sure ports aren't already in use
- Check service logs for errors

**Can't see products?**
- Make sure Product Catalog Service is running
- Check Eureka dashboard - service should be registered
- Try: `curl http://localhost:8080/api/products`

## 🎯 Minimal Setup (Just to See Frontend)

If you just want to see the UI without full backend:

```bash
cd frontend/tcg-shop-frontend
npm install
npm run dev
```

Open http://localhost:3000 - You'll see the UI, but API calls will fail (that's expected).

