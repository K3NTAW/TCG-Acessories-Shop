# 3D Printed TCG Accessories Shop

Eine Microservices-basierte E-Commerce-Plattform für 3D-gedruckte Trading Card Game Accessoires.

## 📋 Projektübersicht

Dieses Projekt implementiert eine vollständige Shop-Anwendung für 3D-gedruckte TCG-Accessoires (Deck Boxes, Card Holders, Storage Solutions, etc.) mit einer Microservices-Architektur.

## 🏗️ Architektur

### Core Infrastructure
- **API Gateway**: Spring Cloud Gateway (Single Entry Point)
- **Service Discovery**: Eureka Server
- **Message Broker**: Apache Kafka

### Business Services
- **Product Catalog Service**: Produktverwaltung (MySQL)
- **Cart Service**: Warenkorb-Verwaltung (Redis)
- **Customer Service**: Kundenverwaltung & Authentifizierung (MySQL)
- **Order Service**: Bestellverwaltung mit Circuit Breaker (MySQL)
- **Payment Service**: Zahlungsverarbeitung (Mockup)
- **Manufacturing Service**: 3D-Druck-Job-Verwaltung (MySQL/Mockup)
- **Review Service**: Produktbewertungen (MySQL/Mockup)

### Frontend
- **React Frontend**: TypeScript, shadcn/ui Components

## 🚀 Quick Start

### Voraussetzungen
- Java 17+
- Maven 3.8+
- Docker & Docker Compose
- Node.js 18+ (für Frontend)

### Lokale Entwicklung

1. **Docker Services starten**:
```bash
cd docker
docker-compose up -d
```

2. **Services starten** (in dieser Reihenfolge):
   ```bash
   # Terminal 1: Eureka Server
   cd services/eureka-server && mvn spring-boot:run
   
   # Terminal 2: API Gateway
   cd services/api-gateway && mvn spring-boot:run
   
   # Terminal 3: Product Catalog Service
   cd services/product-catalog-service && mvn spring-boot:run
   
   # Terminal 4: Cart Service
   cd services/cart-service && mvn spring-boot:run
   
   # Terminal 5: Customer Service
   cd services/customer-service && mvn spring-boot:run
   ```

3. **Frontend starten**:
```bash
cd frontend/tcg-shop-frontend
npm install
npm run dev
```

**Siehe [SETUP.md](./SETUP.md) für detaillierte Anweisungen.**

## 📚 Dokumentation

- [Projektdefinition](./PROJEKTDEFINITION.md)
- [Architektur-Dokumentation](./docs/ARCHITEKTUR.md)
- [Reflexion](./docs/REFLEXION.md)

## 🧪 Testing

- Unit Tests: JUnit 5
- Integration Tests: Spring Boot Test
- API Tests: Postman (automatisiert in CI/CD)

## 🔄 CI/CD

GitHub Actions Pipeline:
- **Build**: Maven Build für alle Services
- **Test**: Unit & Integration Tests
- **Deploy**: Docker Images zu Registry, Deployment zu Cloud

## 📝 API Dokumentation

Swagger UI verfügbar unter:
- API Gateway: `http://localhost:8080/swagger-ui.html`
- Einzelne Services: `http://localhost:{port}/swagger-ui.html`

## 👥 Autoren

[Ihre Namen hier]

## 📄 Lizenz

Projekt für Bildungszwecke - Modul 321-324

# TCG-Acessories-Shop
