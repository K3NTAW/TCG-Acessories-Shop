# Projektdefinition & Architektur

## 🎯 Projektidee

**TCG Accessories Shop** ist eine Microservices-basierte E-Commerce-Plattform für den Handel mit 3D-gedruckten Trading Card Game (TCG) Accessoires. Die Plattform ermöglicht es TCG-Spielern, personalisierte und standardisierte Accessoires für ihre Karten-Sammlungen zu bestellen.

### Kernfunktionalität

- **Produktkatalog**: 3D-gedruckte Accessoires (Deck Boxes, Card Holders, Storage Solutions, Playmats)
- **Kategorien**: Deck Boxes, Holders, Storage, Playmats & Accessories, Custom Designs
- **Kundenverwaltung**: Registrierung, Profile, Authentifizierung (JWT)
- **Bestellprozess**: Warenkorb, Checkout, Bestellverfolgung
- **Asynchrone Verarbeitung**: Event-basierte Order-Processing mit Kafka
- **Circuit Breaker**: Resilience-Pattern für Payment Service Integration

## 🏗️ Architekturskizze

### Gesamtarchitektur

```mermaid
graph TB
    subgraph "Client Layer"
        FE[React Frontend<br/>Port: 3000]
    end
    
    subgraph "API Gateway Layer"
        GW[Spring Cloud Gateway<br/>Port: 8080]
    end
    
    subgraph "Service Discovery"
        EUREKA[Eureka Server<br/>Port: 8761]
    end
    
    subgraph "Business Services"
        PC[Product Catalog<br/>Port: 8081<br/>MySQL]
        CART[Cart Service<br/>Port: 8082<br/>Redis]
        CUST[Customer Service<br/>Port: 8083<br/>MySQL]
        ORD[Order Service<br/>Port: 8084<br/>MySQL]
        PAY[Payment Service<br/>Port: 8085<br/>Mockup]
        MFG[Manufacturing Service<br/>Port: 8086<br/>MySQL]
        REV[Review Service<br/>Port: 8087<br/>MySQL]
    end
    
    subgraph "Messaging Layer"
        KAFKA[Apache Kafka<br/>Port: 9092]
        ZK[Zookeeper<br/>Port: 2181]
    end
    
    subgraph "Data Layer"
        MYSQL1[(MySQL<br/>Product DB<br/>Port: 3306)]
        MYSQL2[(MySQL<br/>Customer DB<br/>Port: 3307)]
        MYSQL3[(MySQL<br/>Order DB<br/>Port: 3308)]
        REDIS[(Redis<br/>Cart Cache<br/>Port: 6379)]
    end
    
    FE -->|HTTP/REST| GW
    GW -->|Routing| PC
    GW -->|Routing| CART
    GW -->|Routing| CUST
    GW -->|Routing| ORD
    GW -->|Routing| PAY
    GW -->|Routing| MFG
    GW -->|Routing| REV
    
    PC -->|Service Discovery| EUREKA
    CART -->|Service Discovery| EUREKA
    CUST -->|Service Discovery| EUREKA
    ORD -->|Service Discovery| EUREKA
    PAY -->|Service Discovery| EUREKA
    MFG -->|Service Discovery| EUREKA
    REV -->|Service Discovery| EUREKA
    
    PC -->|Persist| MYSQL1
    CART -->|Cache| REDIS
    CUST -->|Persist| MYSQL2
    ORD -->|Persist| MYSQL3
    MFG -->|Persist| MYSQL3
    REV -->|Persist| MYSQL3
    
    ORD -->|Circuit Breaker| PAY
    ORD -->|Publish Events| KAFKA
    PAY -->|Publish Events| KAFKA
    MFG -->|Consume Events| KAFKA
    MFG -->|Publish Events| KAFKA
    
    KAFKA -->|Coordination| ZK
    
    style FE fill:#e1f5ff
    style GW fill:#fff4e1
    style EUREKA fill:#ffe1f5
    style KAFKA fill:#e1ffe1
    style REDIS fill:#ffcccc
```

### Request Flow Diagramm

```mermaid
sequenceDiagram
    participant U as User
    participant FE as Frontend
    participant GW as API Gateway
    participant PC as Product Catalog
    participant CART as Cart Service
    participant CUST as Customer Service
    participant ORD as Order Service
    participant PAY as Payment Service
    participant KAFKA as Kafka
    participant MFG as Manufacturing
    
    U->>FE: Browse Products
    FE->>GW: GET /api/products
    GW->>PC: GET /products
    PC-->>GW: Product List
    GW-->>FE: Product List
    FE-->>U: Display Products
    
    U->>FE: Add to Cart
    FE->>GW: POST /api/cart/{sessionId}/items
    GW->>CART: POST /cart/{sessionId}/items
    CART-->>GW: Cart Updated
    GW-->>FE: Cart Updated
    FE-->>U: Item Added
    
    U->>FE: Login
    FE->>GW: POST /api/auth/login
    GW->>CUST: POST /auth/login
    CUST-->>GW: JWT Token
    GW-->>FE: JWT Token
    FE-->>U: Logged In
    
    U->>FE: Checkout
    FE->>GW: POST /api/orders
    GW->>ORD: POST /orders
    ORD->>CUST: Validate Customer
    ORD->>CART: Get Cart Items
    ORD->>PAY: Process Payment (Circuit Breaker)
    PAY-->>ORD: Payment Success
    ORD->>KAFKA: Publish order-created
    ORD-->>GW: Order Created
    GW-->>FE: Order Created
    FE-->>U: Order Confirmed
    
    KAFKA->>MFG: Consume order-created
    MFG->>MFG: Create Print Job
    MFG->>KAFKA: Publish manufacturing-job-created
    KAFKA->>ORD: Consume manufacturing-job-created
    ORD->>ORD: Update Order Status
```

### Deployment-Architektur

```mermaid
graph TB
    subgraph "GitHub Repository"
        REPO[Source Code]
        GHACTIONS[GitHub Actions CI/CD]
    end
    
    subgraph "Container Registry"
        GHCR[GitHub Container Registry<br/>ghcr.io/k3ntaw/tcg-shop-*]
    end
    
    subgraph "Cloud Platform / Local"
        LB[Load Balancer]
        subgraph "Docker Compose Stack"
            GW[API Gateway]
            EUREKA[Eureka Server]
            PC[Product Catalog]
            CART[Cart Service]
            CUST[Customer Service]
            ORD[Order Service]
            PAY[Payment Service]
            MFG[Manufacturing]
            REV[Review Service]
            FE[Frontend]
        end
        
        subgraph "Infrastructure"
            MYSQL[(MySQL Databases)]
            REDIS[(Redis)]
            KAFKA[(Kafka + Zookeeper)]
        end
    end
    
    REPO -->|Push| GHACTIONS
    GHACTIONS -->|Build & Test| GHACTIONS
    GHACTIONS -->|Push Images| GHCR
    GHCR -->|Pull| LB
    LB -->|Route| GW
    GW -->|Service Discovery| EUREKA
    GW -->|Route| PC
    GW -->|Route| CART
    GW -->|Route| CUST
    GW -->|Route| ORD
    GW -->|Route| PAY
    GW -->|Route| MFG
    GW -->|Route| REV
    GW -->|Route| FE
    
    PC -->|Persist| MYSQL
    CART -->|Cache| REDIS
    CUST -->|Persist| MYSQL
    ORD -->|Persist| MYSQL
    MFG -->|Persist| MYSQL
    REV -->|Persist| MYSQL
    
    ORD -->|Events| KAFKA
    PAY -->|Events| KAFKA
    MFG -->|Events| KAFKA
    
    style GHACTIONS fill:#e1f5ff
    style GHCR fill:#fff4e1
    style LB fill:#ffe1f5
```

## 📋 Workflow im Projekt

### 1. Entwicklungs-Workflow

```mermaid
graph LR
    A[Local Development] -->|Code| B[Git Commit]
    B -->|Push| C[GitHub Repository]
    C -->|Trigger| D[GitHub Actions]
    D -->|Build| E[Maven Build]
    E -->|Test| F[Unit Tests]
    F -->|Package| G[Docker Build]
    G -->|Push| H[Container Registry]
    H -->|Deploy| I[Cloud/Local]
    
    style D fill:#e1f5ff
    style H fill:#fff4e1
```

### 2. Git-Workflow

```mermaid
graph TB
    subgraph "Branches"
        MAIN[main branch]
        DEV[develop branch]
        FEAT[feature/* branches]
    end
    
    subgraph "Process"
        COMMIT[Commit Changes]
        PUSH[Push to Branch]
        PR[Create Pull Request]
        REVIEW[Code Review]
        MERGE[Merge to main]
        DEPLOY[Auto Deploy]
    end
    
    FEAT -->|Create| FEAT
    FEAT -->|Work| COMMIT
    COMMIT -->|Push| PUSH
    PUSH -->|Create| PR
    PR -->|Review| REVIEW
    REVIEW -->|Approve| MERGE
    MERGE -->|Trigger| DEPLOY
    
    style MAIN fill:#e1f5ff
    style DEV fill:#fff4e1
    style FEAT fill:#ffe1f5
```

### 3. Deployment-Workflow

```mermaid
graph TB
    subgraph "CI/CD Pipeline"
        BUILD[Build Stage<br/>Maven Package]
        TEST[Test Stage<br/>Unit + Integration]
        DOCKER[Docker Build<br/>Multi-stage]
        PUSH[Push to Registry<br/>GHCR]
        DEPLOY[Deploy<br/>Docker Compose]
    end
    
    BUILD -->|JAR Files| TEST
    TEST -->|Pass| DOCKER
    TEST -->|Fail| STOP[Stop Pipeline]
    DOCKER -->|Images| PUSH
    PUSH -->|Success| DEPLOY
    DEPLOY -->|Running| HEALTH[Health Check]
    HEALTH -->|OK| DONE[Deployment Complete]
    HEALTH -->|Fail| ROLLBACK[Rollback]
    
    style BUILD fill:#e1f5ff
    style TEST fill:#fff4e1
    style DOCKER fill:#ffe1f5
    style DEPLOY fill:#e1ffe1
```

## 🎨 User Interface

Die Benutzeroberfläche bietet:

- **Modernes, responsives Design** mit TailwindCSS
- **Produktkatalog** mit Filter- und Suchfunktionen
- **Warenkorb** mit Real-Time Updates (Redis-basiert)
- **Checkout-Prozess** mit Formular-Validierung
- **Bestellhistorie** und Tracking
- **Produktbewertungen** und Rezensionen

### Frontend-Seiten

- **Home Page**: Übersicht und Featured Products
- **Products Page**: Produktkatalog mit Kategorien
- **Product Detail Page**: Einzelproduktansicht mit Bewertungen
- **Cart Page**: Warenkorb-Verwaltung
- **Checkout Page**: Bestellabwicklung
- **Login/Register**: Authentifizierung

## 🔧 Technologie-Stack

### Frontend

- **React 18**: Moderne Component-basierte UI
- **TypeScript**: Type-Safety für robuste Entwicklung
- **Vite**: Schneller Build-Prozess
- **TailwindCSS**: Utility-First CSS Framework
- **Shadcn/ui**: Hochwertige UI-Komponenten
- **Axios**: HTTP Client für API-Kommunikation
- **React Router**: Client-side Routing

### Backend

- **Spring Boot 3.3.6**: Enterprise Java Framework
- **Spring Data JPA**: Datenbank-Abstraktion
- **Spring Cloud Gateway**: API Gateway mit Resilience
- **Netflix Eureka**: Service Discovery
- **Apache Kafka**: Event Streaming Platform
- **Resilience4j**: Circuit Breaker Pattern
- **MySQL 8.0**: Relationale Datenbank
- **Redis**: In-Memory Cache für Warenkorb

### DevOps

- **Docker**: Container-Virtualisierung
- **Docker Compose**: Multi-Container Orchestrierung
- **Maven**: Build-Management für Java
- **npm**: Package Management für Frontend
- **GitHub Actions**: CI/CD Pipeline
- **GitHub Container Registry**: Docker Image Registry

### Testing & Quality

- **JUnit 5**: Unit Testing Framework
- **Mockito**: Mocking Framework
- **AssertJ**: Fluent Assertions
- **Postman**: API Testing
- **Spring Boot Test**: Integration Testing

## 📊 Microservices Übersicht

| Service      | Port | Funktion                 | Technologie            | Datenbank |
| ------------ | ---- | ------------------------ | ---------------------- | --------- |
| **Frontend** | 3000 | User Interface           | React + TypeScript     | -         |
| **Gateway**  | 8080 | API Gateway, Routing     | Spring Cloud Gateway   | -         |
| **Eureka**   | 8761 | Service Discovery        | Netflix Eureka         | -         |
| **Product**  | 8081 | Produktverwaltung        | Spring Boot            | MySQL     |
| **Cart**     | 8082 | Warenkorb-Verwaltung     | Spring Boot            | Redis     |
| **Customer** | 8083 | Kundenverwaltung, Auth   | Spring Boot            | MySQL     |
| **Order**    | 8084 | Bestellverwaltung        | Spring Boot            | MySQL     |
| **Payment**  | 8085 | Zahlungsverarbeitung    | Spring Boot (Mockup)   | In-Memory |
| **Manufacturing** | 8086 | 3D-Druck-Jobs          | Spring Boot            | MySQL     |
| **Review**   | 8087 | Produktbewertungen       | Spring Boot            | MySQL     |
| **MySQL**    | 3306-3308 | Datenpersistierung       | MySQL 8.0              | -         |
| **Redis**    | 6379 | Warenkorb-Cache          | Redis 7                | -         |
| **Kafka**    | 9092 | Event Streaming          | Apache Kafka           | -         |
| **Zookeeper** | 2181 | Kafka Coordination       | Zookeeper              | -         |

## 🎯 Architektur-Entscheidungen

### Warum Microservices?

1. **Skalierbarkeit**: Einzelne Services können unabhängig skaliert werden
   - Product Catalog Service kann bei hoher Last skaliert werden
   - Cart Service nutzt Redis für schnelle Zugriffe
2. **Wartbarkeit**: Kleinere, fokussierte Codebases
   - Jeder Service hat klare Verantwortlichkeiten
   - Unabhängige Entwicklung und Deployment
3. **Technologie-Flexibilität**: Verschiedene Tech-Stacks pro Service möglich
   - Redis für Cart (NoSQL, in-memory)
   - MySQL für persistente Daten (relational)
4. **Fehler-Isolation**: Probleme in einem Service betreffen nicht das gesamte System
   - Circuit Breaker Pattern schützt vor Kaskadenfehlern

### Warum API Gateway?

1. **Single Entry Point**: Einheitlicher Zugriffspunkt für Clients
   - Alle Requests gehen über Port 8080
   - Zentrale Konfiguration von Routing
2. **Cross-Cutting Concerns**: CORS, Authentication, Rate Limiting zentral
   - JWT-Validierung im Gateway
   - Request/Response Transformation
3. **Circuit Breaker**: Fehlertoleranz bei Service-Ausfällen
   - Integration mit Resilience4j
4. **Load Balancing**: Automatische Lastverteilung über Eureka

### Warum Kafka?

1. **Asynchrone Verarbeitung**: Entkopplung von Services
   - Order Service muss nicht warten, bis Manufacturing Service bereit ist
   - Event-driven Architecture
2. **Event Sourcing**: Vollständige Event-History
   - Alle Events werden persistent gespeichert
   - Replay-Funktionalität möglich
3. **Skalierbarkeit**: Millionen Events pro Sekunde
   - Hoher Durchsatz für Bestellungen
4. **Reliability**: Garantierte Event-Zustellung
   - At-least-once Delivery
   - Consumer Groups für parallele Verarbeitung

### Warum Eureka?

1. **Service Discovery**: Automatische Service-Registrierung
   - Services registrieren sich selbständig
   - Dynamische Service-Erkennung
2. **Health Monitoring**: Automatische Erkennung von Service-Ausfällen
   - Health Checks integriert
   - Automatische Deregistrierung bei Ausfall
3. **Load Balancing**: Integration mit Spring Cloud LoadBalancer
   - Automatische Lastverteilung
4. **Zero-Configuration**: Services registrieren sich selbständig
   - Keine manuelle Konfiguration nötig

### Warum Circuit Breaker?

1. **Resilience**: System bleibt stabil bei Teilausfällen
   - Payment Service-Ausfall blockiert nicht Bestellungen
   - Fallback-Mechanismen aktivieren sich automatisch
2. **Performance**: Vermeidet Timeouts bei fehlerhaften Services
   - Schnelle Fehlerbehandlung
   - Keine Wartezeiten bei bekannten Fehlern
3. **User Experience**: Schnelle Fehlerbehandlung
   - Bestellungen werden als "Payment Pending" markiert
   - Manuelle Nachbearbeitung möglich

## 📈 Projektziele

### Funktionale Ziele

- ✅ Vollständiger E-Commerce-Workflow implementiert
- ✅ Produktverwaltung mit Kategorien und Varianten
- ✅ Kundenverwaltung mit Registrierung und Authentifizierung
- ✅ Warenkorb-Verwaltung mit Redis-Persistierung
- ✅ Bestellprozess mit Event-basierter Verarbeitung
- ✅ Circuit Breaker für Payment Service Integration
- ✅ Kafka-basierte asynchrone Kommunikation

### Nicht-funktionale Ziele

- ✅ Microservices-Architektur mit klaren Grenzen
- ✅ Hohe Verfügbarkeit durch Circuit Breaker
- ✅ Skalierbarkeit durch Container und Service Discovery
- ✅ Testabdeckung mit Unit und Integration Tests
- ✅ API-Dokumentation mit Postman Collections
- ✅ CI/CD Pipeline mit GitHub Actions
- ✅ Docker-basierte Deployment-Strategie

### Lernziele

- ✅ Praktische Erfahrung mit Microservices-Architektur
- ✅ Event-Driven Architecture mit Kafka
- ✅ API Gateway Pattern Implementation
- ✅ Circuit Breaker Pattern mit Resilience4j
- ✅ Container-Orchestrierung mit Docker Compose
- ✅ Service Discovery mit Eureka
- ✅ Moderne Frontend-Entwicklung mit React
- ✅ CI/CD Pipeline Setup und Deployment

## 🔄 Event Flow (Kafka Topics)

```mermaid
graph LR
    subgraph "Producers"
        ORD[Order Service]
        PAY[Payment Service]
        MFG[Manufacturing Service]
    end
    
    subgraph "Kafka Topics"
        OC[order-created]
        PP[payment-processed]
        OSU[order-status-updated]
        MJC[manufacturing-job-created]
        IU[inventory-updated]
    end
    
    subgraph "Consumers"
        MFGC[Manufacturing Service]
        ORDC[Order Service]
        PCC[Product Catalog]
    end
    
    ORD -->|Publish| OC
    PAY -->|Publish| PP
    ORD -->|Publish| OSU
    MFG -->|Publish| MJC
    MFG -->|Publish| IU
    
    OC -->|Consume| MFGC
    PP -->|Consume| MFGC
    PP -->|Consume| ORDC
    MJC -->|Consume| ORDC
    IU -->|Consume| PCC
    
    style ORD fill:#e1f5ff
    style PAY fill:#fff4e1
    style MFG fill:#ffe1f5
    style OC fill:#e1ffe1
    style PP fill:#e1ffe1
    style MJC fill:#e1ffe1
```

## 📝 Datenbank-Design

### Product Catalog Service (MySQL)

```mermaid
erDiagram
    PRODUCT ||--o{ PRODUCT_VARIANT : has
    PRODUCT {
        bigint id PK
        string name
        text description
        string category
        decimal price
        int stock_quantity
        string image_url
        datetime created_at
        datetime updated_at
    }
    PRODUCT_VARIANT {
        bigint id PK
        bigint product_id FK
        string variant_name
        decimal price_modifier
        int stock_quantity
    }
```

### Customer Service (MySQL)

```mermaid
erDiagram
    CUSTOMER ||--o{ ADDRESS : has
    CUSTOMER {
        bigint id PK
        string email UK
        string password_hash
        string first_name
        string last_name
        string phone
        datetime created_at
        datetime updated_at
    }
    ADDRESS {
        bigint id PK
        bigint customer_id FK
        string street
        string city
        string postal_code
        string country
        boolean is_default
    }
```

### Order Service (MySQL)

```mermaid
erDiagram
    ORDER ||--o{ ORDER_ITEM : contains
    ORDER {
        bigint id PK
        bigint customer_id FK
        string order_number UK
        string status
        decimal total_amount
        bigint shipping_address_id
        datetime created_at
        datetime updated_at
    }
    ORDER_ITEM {
        bigint id PK
        bigint order_id FK
        bigint product_id
        int quantity
        decimal unit_price
        decimal subtotal
    }
```

---

**Aufgabe 1 erfüllt**: ✅ Architekturskizze erstellt, Projektidee beschrieben, Workflow dokumentiert

_Nächstes Dokument_: [docs/ARCHITEKTUR.md](docs/ARCHITEKTUR.md) - Technische Vertiefung

