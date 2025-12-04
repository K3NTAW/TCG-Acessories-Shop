# What is the Frontend?

## 🎯 Overview

The **Frontend** is the user interface (UI) that customers interact with. It's what you see in your web browser when you visit the shop.

## 🏗️ In Our Project

### Technology Stack
- **React** - JavaScript library for building user interfaces
- **TypeScript** - Typed JavaScript for better code quality
- **Vite** - Fast build tool and development server
- **shadcn/ui** - Modern UI component library
- **Tailwind CSS** - Utility-first CSS framework
- **React Router** - For navigation between pages

### What It Does

The frontend is the **client-side** of the application. It:

1. **Displays Products** - Shows 3D printed TCG accessories
2. **Shopping Cart** - Lets users add/remove items
3. **User Authentication** - Login and registration pages
4. **Product Browsing** - Browse by category, search products
5. **Order Management** - View orders, checkout process

### Architecture

```
┌─────────────────────────────────┐
│      Frontend (React)            │
│      Port: 3000                  │
│                                  │
│  - Home Page                     │
│  - Products Page                 │
│  - Product Details               │
│  - Shopping Cart                 │
│  - Login/Register                │
└──────────┬───────────────────────┘
           │
           │ HTTP/REST API Calls
           │
┌──────────▼───────────────────────┐
│      API Gateway                 │
│      Port: 8080                  │
│                                  │
│  Routes to:                      │
│  - Product Catalog Service       │
│  - Cart Service                  │
│  - Customer Service              │
│  - Order Service                 │
└──────────────────────────────────┘
```

## 📁 Frontend Structure

```
frontend/tcg-shop-frontend/
├── src/
│   ├── pages/              # Different pages/screens
│   │   ├── HomePage.tsx
│   │   ├── ProductsPage.tsx
│   │   ├── ProductDetailPage.tsx
│   │   ├── CartPage.tsx
│   │   ├── LoginPage.tsx
│   │   └── RegisterPage.tsx
│   │
│   ├── components/         # Reusable UI components
│   │   ├── ui/            # shadcn/ui components
│   │   │   ├── button.tsx
│   │   │   ├── card.tsx
│   │   │   └── ...
│   │   └── layout/        # Layout components
│   │       └── Navbar.tsx
│   │
│   ├── lib/               # Utilities and API clients
│   │   ├── api.ts         # API calls to backend
│   │   └── utils.ts       # Helper functions
│   │
│   ├── App.tsx            # Main app component
│   └── main.tsx           # Entry point
│
├── package.json           # Dependencies
├── vite.config.ts         # Build configuration
└── tailwind.config.js     # Styling configuration
```

## 🔄 How It Works

### 1. User Opens Browser
- Goes to `http://localhost:3000`
- Frontend loads and displays the home page

### 2. User Browses Products
- Frontend calls: `GET /api/products` → API Gateway → Product Catalog Service
- Displays products in a grid

### 3. User Adds to Cart
- Frontend calls: `POST /api/cart/{sessionId}/items` → API Gateway → Cart Service
- Cart updates in real-time

### 4. User Checks Out
- Frontend calls: `POST /api/orders` → API Gateway → Order Service
- Order is created, payment processed, etc.

## 🎨 What Users See

### Pages:

1. **Home Page** (`/`)
   - Welcome message
   - Product categories (Deck Boxes, Card Holders, etc.)
   - "Shop Now" button

2. **Products Page** (`/products`)
   - Grid of all products
   - Product cards with name, price, description
   - "View Details" buttons

3. **Product Detail Page** (`/products/:id`)
   - Full product information
   - "Add to Cart" button
   - Product image, price, description

4. **Shopping Cart** (`/cart`)
   - List of items in cart
   - Quantity controls
   - Total price
   - Remove items

5. **Login/Register** (`/login`, `/register`)
   - Forms for authentication
   - JWT token stored in browser

## 🔌 Communication with Backend

The frontend **never directly** talks to microservices. It always goes through the **API Gateway**:

```javascript
// Example: Getting products
axios.get('http://localhost:8080/api/products')
  ↓
API Gateway (port 8080)
  ↓
Product Catalog Service (port 8081)
  ↓
Returns products
  ↓
Frontend displays them
```

## 🚀 How to Start It

```bash
cd frontend/tcg-shop-frontend
npm install    # First time only
npm run dev    # Start development server
```

Then open: **http://localhost:3000**

## 📱 Features

- ✅ **Responsive Design** - Works on mobile, tablet, desktop
- ✅ **Modern UI** - Clean, professional design with shadcn/ui
- ✅ **Fast** - Built with Vite for quick development
- ✅ **Type-Safe** - TypeScript prevents errors
- ✅ **Dark Mode Ready** - Tailwind CSS supports dark mode

## 🎯 Summary

**Frontend = What the user sees and interacts with**

- Built with React + TypeScript
- Runs in the browser (port 3000)
- Communicates with backend via API Gateway
- Provides the shopping experience for customers

It's the "face" of your application - everything users see and click on!

