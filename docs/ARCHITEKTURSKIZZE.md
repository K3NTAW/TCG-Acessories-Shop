# Grafische Architekturskizze

## System-Architektur (Text-basiert)

```
┌─────────────────────────────────────────────────────────────────────────┐
│                           CLIENT LAYER                                   │
│                                                                           │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │                    React Frontend                                │   │
│  │                    (Port: 3000)                                  │   │
│  │  - Product Browsing                                               │   │
│  │  - Shopping Cart                                                  │   │
│  │  - Order Management                                               │   │
│  │  - User Profile                                                   │   │
│  └─────────────────────────────────────────────────────────────────┘   │
└───────────────────────────────┬─────────────────────────────────────────┘
                                │
                                │ HTTPS/REST
                                │
┌───────────────────────────────▼─────────────────────────────────────────┐
│                        API GATEWAY LAYER                                  │
│                                                                           │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │              Spring Cloud Gateway                                │   │
│  │                    (Port: 8080)                                  │   │
│  │  - Routing                                                       │   │
│  │  - Load Balancing                                                │   │
│  │  - Authentication (JWT)                                          │   │
│  │  - Rate Limiting                                                 │   │
│  │  - Request/Response Transformation                              │   │
│  └─────────────────────────────────────────────────────────────────┘   │
└───────┬───────────┬───────────┬───────────┬───────────┬───────────────┘
        │           │           │           │           │
        │           │           │           │           │
┌───────▼───────────▼───────────▼───────────▼───────────▼───────────────┐
│                      SERVICE DISCOVERY                                   │
│                                                                           │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │              Eureka Server                                       │   │
│  │                    (Port: 8761)                                  │   │
│  │  - Service Registry                                              │   │
│  │  - Health Checks                                                 │   │
│  │  - Load Balancing Info                                           │   │
│  └─────────────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────┐
│                        BUSINESS SERVICES LAYER                           │
│                                                                           │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  ┌────────────┐ │
│  │   Product    │  │     Cart      │  │   Customer   │  │   Order   │ │
│  │   Catalog    │  │    Service    │  │   Service    │  │  Service  │ │
│  │   Service    │  │               │  │              │  │           │ │
│  │  Port: 8081  │  │  Port: 8082   │  │  Port: 8083  │  │Port: 8084 │ │
│  │              │  │               │  │              │  │           │ │
│  │    MySQL     │  │     Redis     │  │    MySQL     │  │   MySQL   │ │
│  └──────────────┘  └──────────────┘  └──────────────┘  └─────┬──────┘ │
│                                                                 │        │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐        │        │
│  │   Payment    │  │Manufacturing │  │    Review    │        │        │
│  │   Service    │  │   Service    │  │   Service    │        │        │
│  │              │  │              │  │              │        │        │
│  │  Port: 8085  │  │  Port: 8086  │  │  Port: 8087  │        │        │
│  │              │  │              │  │              │        │        │
│  │   Mockup     │  │ MySQL/Mockup │  │ MySQL/Mockup │        │        │
│  └──────────────┘  └──────────────┘  └──────────────┘        │        │
│                                                                 │        │
│  Circuit Breaker: Order Service → Payment Service              │        │
└────────────────────────────────────────────────────────────────┼────────┘
                                                                  │
┌─────────────────────────────────────────────────────────────────▼───────┐
│                      MESSAGING LAYER                                     │
│                                                                           │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │              Apache Kafka                                        │   │
│  │                    (Port: 9092)                                   │   │
│  │                                                                   │   │
│  │  Topics:                                                         │   │
│  │  • order-created                                                 │   │
│  │  • payment-processed                                             │   │
│  │  • order-status-updated                                          │   │
│  │  • manufacturing-job-created                                      │   │
│  │  • inventory-updated                                              │   │
│  │                                                                   │   │
│  │  Producers: Order, Payment, Manufacturing                        │   │
│  │  Consumers: Manufacturing, Order, Product Catalog                 │   │
│  └─────────────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────┐
│                        DATA LAYER                                        │
│                                                                           │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  ┌────────────┐ │
│  │   MySQL      │  │   MySQL      │  │   MySQL      │  │   Redis    │ │
│  │  Product     │  │  Customer    │  │    Order     │  │    Cart    │ │
│  │  Catalog DB  │  │     DB       │  │     DB       │  │   Cache    │ │
│  │              │  │              │  │              │  │            │ │
│  │  Port: 3306  │  │  Port: 3307  │  │  Port: 3308  │  │ Port: 6379│ │
│  └──────────────┘  └──────────────┘  └──────────────┘  └────────────┘ │
└─────────────────────────────────────────────────────────────────────────┘
```

## Kommunikations-Flows

### 1. Produkt-Browsing Flow
```
Frontend → API Gateway → Product Catalog Service → MySQL
         ←────────────── ←───────────────────────────
```

### 2. Warenkorb-Flow
```
Frontend → API Gateway → Cart Service → Redis
         ←────────────── ←───────────────
```

### 3. Bestell-Flow (Synchron + Asynchron)
```
Frontend → API Gateway → Order Service
                           │
                           ├─→ Customer Service (REST)
                           ├─→ Payment Service (REST + Circuit Breaker)
                           │
                           └─→ Kafka: order-created
                                    │
                                    └─→ Manufacturing Service (Kafka Consumer)
                                         └─→ Kafka: manufacturing-job-created
                                              └─→ Order Service (Status Update)
```

### 4. Circuit Breaker Pattern
```
Order Service → Payment Service
     │              │
     │         [Success] ──┐
     │              │       │
     │         [Failure] ──┤
     │              │       │
     │         [Timeout] ──┤
     │              │       │
     └── Circuit Breaker ───┘
              │
         [Open State]
              │
         Fallback: Mark order as "Payment Pending"
```

## Deployment-Architektur

```
┌─────────────────────────────────────────────────────────────┐
│                    GitHub Repository                          │
│                                                               │
│  ┌─────────────────────────────────────────────────────┐   │
│  │              GitHub Actions CI/CD                    │   │
│  │                                                       │   │
│  │  Stages:                                             │   │
│  │  1. Build (Maven)                                     │   │
│  │  2. Test (Unit + Integration)                         │   │
│  │  3. Build Docker Images                               │   │
│  │  4. Push to Registry (GitHub Container Registry)     │   │
│  │  5. Deploy to Cloud (Fly.io / AWS / Azure)            │   │
│  └─────────────────────────────────────────────────────┘   │
└───────────────────────────────┬───────────────────────────────┘
                                │
                                │ Docker Images
                                │
┌───────────────────────────────▼───────────────────────────────┐
│              Container Registry (GitHub Container Registry)     │
│              ghcr.io/username/tcg-shop-{service}:{version}     │
└───────────────────────────────┬───────────────────────────────┘
                                │
                                │ Pull & Deploy
                                │
┌───────────────────────────────▼───────────────────────────────┐
│                    Cloud Platform (Fly.io / AWS / Azure)       │
│                                                               │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐       │
│  │   Service    │  │   Service    │  │   Service    │       │
│  │  Container   │  │  Container   │  │  Container   │       │
│  │   (Pod 1)    │  │   (Pod 2)    │  │   (Pod N)    │       │
│  └──────────────┘  └──────────────┘  └──────────────┘       │
│                                                               │
│  Load Balancer → API Gateway → Services                      │
└───────────────────────────────────────────────────────────────┘
```

## Datenbank-Relationships

### Product Catalog Service (MySQL)
```
products
├── id (PK)
├── name
├── description
├── category
├── price
└── stock_quantity
    │
    └── product_variants
        ├── id (PK)
        ├── product_id (FK)
        └── variant_name
```

### Customer Service (MySQL)
```
customers
├── id (PK)
├── email (UNIQUE)
└── password_hash
    │
    └── addresses
        ├── id (PK)
        ├── customer_id (FK)
        └── street, city, postal_code
```

### Order Service (MySQL)
```
orders
├── id (PK)
├── customer_id (FK → Customer Service)
├── order_number
├── status
└── total_amount
    │
    └── order_items
        ├── id (PK)
        ├── order_id (FK)
        ├── product_id (Reference → Product Catalog)
        └── quantity, unit_price
```

## Event Flow (Kafka)

```
┌─────────────┐
│Order Service│
└──────┬──────┘
       │
       │ Publish: order-created
       │
       ▼
┌─────────────────┐
│  Kafka Topic:   │
│  order-created  │
└──────┬──────────┘
       │
       ├─────────────────┐
       │                 │
       ▼                 ▼
┌──────────────┐  ┌──────────────┐
│Manufacturing │  │Notification  │
│   Service    │  │   Service    │
│  (Consumer)  │  │  (Consumer)  │
└──────┬───────┘  └──────────────┘
       │
       │ Publish: manufacturing-job-created
       │
       ▼
┌─────────────────────────┐
│  Kafka Topic:           │
│  manufacturing-job-     │
│  created                │
└──────┬──────────────────┘
       │
       ▼
┌──────────────┐
│Order Service │
│  (Consumer)  │
│  Updates     │
│  Status      │
└──────────────┘
```

## Legende

- **REST API**: Synchronous HTTP Communication
- **Kafka**: Asynchronous Event-Driven Communication
- **Circuit Breaker**: Resilience Pattern
- **Database**: Persistent Storage
- **Cache**: In-Memory Storage (Redis)

