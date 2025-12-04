# Architektur-Dokumentation

## 1. Architektur-Übersicht

### 1.1 System-Architektur

```
┌─────────────────────────────────────────────────────────────┐
│                        Frontend (React)                      │
│                    Port: 3000                                │
└──────────────────────────┬──────────────────────────────────┘
                           │
                           │ HTTP/REST
                           │
┌──────────────────────────▼──────────────────────────────────┐
│                    API Gateway                               │
│              Spring Cloud Gateway                            │
│                    Port: 8080                                │
└──────┬──────────┬──────────┬──────────┬──────────┬──────────┘
       │          │          │          │          │
       │          │          │          │          │
┌──────▼──────┐ ┌─▼──────┐ ┌─▼──────┐ ┌─▼──────┐ ┌─▼──────┐
│   Product   │ │  Cart  │ │Customer │ │ Order  │ │Payment │
│  Catalog    │ │Service │ │ Service │ │Service │ │Service │
│  Service    │ │        │ │         │ │        │ │        │
│  Port: 8081 │ │Port:   │ │Port:    │ │Port:   │ │Port:   │
│             │ │8082    │ │8083     │ │8084    │ │8085    │
│  MySQL      │ │Redis   │ │MySQL    │ │MySQL   │ │Mockup  │
└──────┬──────┘ └────────┘ └────┬────┘ └────┬───┘ └────┬───┘
       │                         │           │          │
       │                         │           │          │
       │                    ┌───▼───────────▼──────────▼───┐
       │                    │      Kafka Message Broker     │
       │                    │      Topics:                  │
       │                    │      - order-created          │
       │                    │      - payment-processed      │
       │                    │      - manufacturing-job       │
       │                    └───────────┬───────────────────┘
       │                                │
       │                    ┌───────────▼───────────┐
       │                    │  Manufacturing Service│
       │                    │  Port: 8086           │
       │                    │  MySQL/Mockup         │
       │                    └───────────────────────┘
       │
       │                    ┌───────────▼───────────┐
       │                    │    Review Service     │
       │                    │    Port: 8087         │
       │                    │    MySQL/Mockup       │
       │                    └───────────────────────┘
       │
┌──────▼──────────────────────────────────────────────────────┐
│              Eureka Service Discovery                         │
│                    Port: 8761                                │
└──────────────────────────────────────────────────────────────┘
```

### 1.2 Kommunikations-Patterns

#### Synchron (REST)
- Frontend → API Gateway → Services
- Service-to-Service über Eureka Service Discovery
- Load Balancing durch API Gateway

#### Asynchron (Kafka)
- Order Service → Kafka → Manufacturing Service
- Payment Service → Kafka → Order Service
- Event-driven Architecture für lose Kopplung

## 2. Service-Details

### 2.1 API Gateway
- **Technologie**: Spring Cloud Gateway
- **Funktionen**:
  - Routing basierend auf Pfaden
  - Load Balancing zu Service-Instanzen
  - Authentication/Authorization (JWT Validation)
  - Rate Limiting
  - Request/Response Transformation

**Routing-Konfiguration**:
```
/api/products/**     → Product Catalog Service
/api/cart/**         → Cart Service
/api/customers/**    → Customer Service
/api/auth/**         → Customer Service
/api/orders/**       → Order Service
/api/payments/**     → Payment Service
/api/manufacturing/** → Manufacturing Service
/api/reviews/**      → Review Service
```

### 2.2 Eureka Service Discovery
- **Technologie**: Spring Cloud Netflix Eureka
- **Funktionen**:
  - Automatische Service-Registrierung
  - Health Checks
  - Service-Lookup für dynamische Service-Erkennung
  - Load Balancing Support

### 2.3 Kafka Topics

| Topic | Producer | Consumer | Zweck |
|-------|----------|----------|-------|
| `order-created` | Order Service | Manufacturing Service | Neue Bestellung → Print-Job erstellen |
| `payment-processed` | Payment Service | Order Service, Manufacturing Service | Zahlung erfolgreich → Bestellung aktivieren |
| `order-status-updated` | Order Service | Frontend (via WebSocket/SSE) | Status-Updates an Frontend |
| `manufacturing-job-created` | Manufacturing Service | Order Service | Print-Job erstellt → Status Update |
| `inventory-updated` | Manufacturing Service | Product Catalog Service | Material-Verbrauch → Lager aktualisieren |

## 3. Datenfluss-Beispiele

### 3.1 Bestellprozess (Happy Path)

```
1. Frontend → API Gateway → Product Catalog Service
   GET /api/products
   ↓
2. Frontend → API Gateway → Cart Service
   POST /api/cart/{sessionId}/items
   ↓
3. Frontend → API Gateway → Customer Service
   POST /api/auth/login
   ↓
4. Frontend → API Gateway → Order Service
   POST /api/orders
   ↓
5. Order Service → Payment Service (mit Circuit Breaker)
   POST /api/payments
   ↓
6. Payment Service → Kafka
   Topic: payment-processed
   ↓
7. Manufacturing Service (Kafka Consumer)
   Erstellt Print-Job
   ↓
8. Manufacturing Service → Kafka
   Topic: manufacturing-job-created
   ↓
9. Order Service (Kafka Consumer)
   Aktualisiert Bestellstatus
```

### 3.2 Circuit Breaker Pattern

**Order Service → Payment Service**:
- **Normal**: Synchroner REST Call
- **Fehlerfall**: Circuit Breaker öffnet nach 5 Fehlversuchen
- **Fallback**: Bestellung wird als "Payment Pending" markiert, manuelle Prüfung möglich
- **Recovery**: Circuit Breaker schließt nach 30 Sekunden, Retry möglich

## 4. Datenbank-Design

### 4.1 Product Catalog Service (MySQL)

```sql
products
├── id (PK)
├── name
├── description
├── category (Deck Box, Holder, Storage, etc.)
├── price
├── stock_quantity
├── image_url
├── created_at
└── updated_at

product_variants
├── id (PK)
├── product_id (FK)
├── variant_name (Color, Size, etc.)
├── price_modifier
└── stock_quantity
```

### 4.2 Cart Service (Redis)

```
Key: cart:{sessionId}
Value: {
  "items": [
    {
      "productId": "123",
      "quantity": 2,
      "price": 29.99
    }
  ],
  "total": 59.98,
  "updatedAt": "2025-12-04T10:00:00Z"
}
TTL: 7 days
```

### 4.3 Customer Service (MySQL)

```sql
customers
├── id (PK)
├── email (UNIQUE)
├── password_hash
├── first_name
├── last_name
├── phone
├── created_at
└── updated_at

addresses
├── id (PK)
├── customer_id (FK)
├── street
├── city
├── postal_code
├── country
└── is_default
```

### 4.4 Order Service (MySQL)

```sql
orders
├── id (PK)
├── customer_id (FK)
├── order_number (UNIQUE)
├── status (PENDING, PROCESSING, PRINTING, SHIPPED, DELIVERED, CANCELLED)
├── total_amount
├── shipping_address_id
├── created_at
└── updated_at

order_items
├── id (PK)
├── order_id (FK)
├── product_id
├── quantity
├── unit_price
└── subtotal
```

## 5. Sicherheits-Architektur

### 5.1 Authentication Flow

```
1. Client → Customer Service
   POST /api/auth/login
   { email, password }
   ↓
2. Customer Service validiert Credentials
   ↓
3. Customer Service generiert JWT Token
   ↓
4. Client erhält JWT Token
   ↓
5. Client sendet JWT Token in Header
   Authorization: Bearer {token}
   ↓
6. API Gateway validiert JWT Token
   ↓
7. Request wird an Service weitergeleitet
```

### 5.2 Security Headers
- HTTPS Enforcement
- CORS Configuration
- XSS Protection
- Content Security Policy

## 6. Deployment-Architektur

### 6.1 Lokale Entwicklung
- Docker Compose für alle Services
- Port-Mapping für direkten Zugriff
- Hot Reload für Development

### 6.2 Production (Cloud)
- Container-basiert (Docker)
- Orchestrierung: Kubernetes oder Docker Swarm (optional)
- Load Balancer vor API Gateway
- Database-as-a-Service (z.B. AWS RDS, Azure Database)
- Redis Cloud Service
- Kafka Cloud Service (z.B. Confluent Cloud)

## 7. Monitoring & Observability

### 7.1 Health Checks
- Spring Boot Actuator `/actuator/health`
- Eureka Health Checks
- Database Connection Monitoring

### 7.2 Logging
- Structured Logging (JSON Format)
- Centralized Logging (optional: ELK Stack)
- Correlation IDs für Request-Tracking

### 7.3 Metrics
- Spring Boot Actuator Metrics
- Custom Business Metrics
- Response Time Tracking

## 8. Skalierungs-Strategien

### 8.1 Horizontal Scaling
- Jeder Service kann unabhängig skaliert werden
- Load Balancing durch API Gateway
- Stateless Services für einfache Skalierung

### 8.2 Database Scaling
- Read Replicas für Product Catalog (hohe Read-Last)
- Redis Cluster für Cart Service (hohe Write-Last)
- Connection Pooling

## 9. Fehlerbehandlung

### 9.1 Retry-Mechanismen
- Transient Failures: Exponential Backoff
- Max Retries: 3
- Timeout: 5 Sekunden

### 9.2 Fallback-Strategien
- Circuit Breaker Fallbacks
- Default Values bei Service-Ausfällen
- Graceful Degradation

### 9.3 Error Responses
```json
{
  "timestamp": "2025-12-04T10:00:00Z",
  "status": 500,
  "error": "Internal Server Error",
  "message": "Service temporarily unavailable",
  "path": "/api/orders",
  "correlationId": "abc-123-def"
}
```

## 10. Architektur-Entscheidungen

### 10.1 Warum Microservices?
- **Unabhängige Entwicklung**: Teams können parallel arbeiten
- **Technologie-Flexibilität**: Verschiedene Services können unterschiedliche Tech-Stacks nutzen
- **Skalierbarkeit**: Nur benötigte Services werden skaliert
- **Fehlertoleranz**: Ausfall eines Services beeinträchtigt nicht das gesamte System

### 10.2 Warum Kafka?
- **Asynchrone Kommunikation**: Entkopplung von Services
- **Event-Driven Architecture**: Reaktive Systeme
- **Scalability**: Hoher Durchsatz
- **Durability**: Events werden persistent gespeichert

### 10.3 Warum Circuit Breaker?
- **Resilience**: System bleibt stabil bei Teilausfällen
- **Performance**: Vermeidet Timeouts bei fehlerhaften Services
- **User Experience**: Schnelle Fehlerbehandlung

### 10.4 Warum API Gateway?
- **Single Entry Point**: Zentrale Konfiguration
- **Security**: Zentrale Authentifizierung
- **Load Balancing**: Automatische Verteilung
- **Monitoring**: Zentrale Metriken

