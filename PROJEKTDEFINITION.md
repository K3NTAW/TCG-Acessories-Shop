# Projektdefinition: 3D Printed TCG Accessories Shop

## 1. Projektübersicht

### 1.1 Idee
Entwicklung einer E-Commerce-Plattform für 3D-gedruckte Trading Card Game (TCG) Accessoires. Die Anwendung ermöglicht es Kunden, personalisierte und standardisierte Accessoires für ihre TCG-Sammlungen zu bestellen.

### 1.2 Zielgruppe
- TCG-Spieler (Magic: The Gathering, Pokémon, Yu-Gi-Oh!, etc.)
- Sammler von Trading Cards
- Hobbyisten, die ihre Decks professionell organisieren möchten

### 1.3 Produktkategorien
- **Deck Boxes**: Verschiedene Größen für Standard, Commander, Modern Decks
- **Card Holders**: Einzelkarten-Halter, Display-Ständer
- **Storage Solutions**: Kartenboxen, Binder-Organizer, Sortierboxen
- **Playmats & Accessories**: Custom Playmat-Halter, Life Counter, Dice Trays
- **Custom Designs**: Personalisierte Accessoires nach Kundenwunsch

## 2. Architektur-Überlegungen

### 2.1 Microservices-Architektur
Die Anwendung wird als Microservices-Architektur implementiert, um:
- **Skalierbarkeit**: Einzelne Services können unabhängig skaliert werden
- **Wartbarkeit**: Klare Trennung der Verantwortlichkeiten
- **Technologische Flexibilität**: Verschiedene Services können unterschiedliche Technologien nutzen
- **Fehlertoleranz**: Ausfall eines Services beeinträchtigt nicht das gesamte System

### 2.2 Workflow: Bestelldurchgang

**Szenario 1: Standard-Bestellung**
1. Kunde durchsucht Produktkatalog (Product Catalog Service)
2. Kunde fügt Produkte zum Warenkorb hinzu (Cart Service)
3. Kunde meldet sich an/registriert sich (Customer Service)
4. Kunde initiiert Bestellung (Order Service)
5. Zahlung wird verarbeitet (Payment Service - Mockup)
6. Bestellung wird an Manufacturing Service weitergegeben (Kafka Event)
7. Manufacturing Service erstellt Print-Job
8. Bestätigung und Tracking-Informationen werden an Kunde gesendet

**Szenario 2: Custom Design Bestellung**
1. Kunde lädt Custom Design hoch (Product Catalog Service)
2. Design wird validiert und Preiskalkulation erfolgt
3. Kunde bestätigt Custom Order (Order Service)
4. Nach Zahlung wird Custom Print-Job erstellt (Manufacturing Service)

## 3. Microservices-Definition

### 3.1 Core Infrastructure Services

#### API Gateway Service
- **Zweck**: Single Entry Point für alle Client-Anfragen
- **Technologie**: Spring Cloud Gateway
- **Funktionen**:
  - Routing zu verschiedenen Microservices
  - Load Balancing
  - Authentication/Authorization
  - Rate Limiting
  - Request/Response Transformation

#### Eureka Service Discovery
- **Zweck**: Service Registry für dynamische Service-Erkennung
- **Technologie**: Spring Cloud Netflix Eureka
- **Funktionen**:
  - Automatische Service-Registrierung
  - Health Checks
  - Service-Lookup für Inter-Service-Kommunikation

#### Kafka Message Broker
- **Zweck**: Asynchrone Kommunikation zwischen Services
- **Technologie**: Apache Kafka
- **Verwendete Topics**:
  - `order-created`: Neue Bestellung wurde erstellt
  - `order-status-updated`: Bestellstatus wurde aktualisiert
  - `payment-processed`: Zahlung wurde verarbeitet
  - `manufacturing-job-created`: Neuer Print-Job wurde erstellt
  - `inventory-updated`: Lagerbestand wurde aktualisiert

### 3.2 Business Services

#### Product Catalog Service
- **Zweck**: Verwaltung des Produktkatalogs (3D-gedruckte Accessoires)
- **Technologie**: Spring Boot, Spring Data JPA, MySQL
- **Datenbank**: MySQL (eigene DB)
- **Funktionen**:
  - Produktverwaltung (CRUD)
  - Kategorisierung (Deck Boxes, Holders, Storage, etc.)
  - Produktsuche und -filterung
  - Custom Design Upload und Validierung
  - Preisverwaltung
- **Endpoints**:
  - `GET /api/products` - Produktliste mit Filterung
  - `GET /api/products/{id}` - Einzelnes Produkt
  - `POST /api/products` - Neues Produkt (Admin)
  - `PUT /api/products/{id}` - Produkt aktualisieren
  - `DELETE /api/products/{id}` - Produkt löschen
  - `POST /api/products/custom/validate` - Custom Design validieren
- **Begründung für eigene DB**: Produktdaten sind kritisch und benötigen persistente Speicherung. Unabhängigkeit von anderen Services gewährleistet.

#### Cart Service
- **Zweck**: Warenkorb-Verwaltung
- **Technologie**: Spring Boot, Redis
- **Datenbank**: Redis (NoSQL, in-memory)
- **Funktionen**:
  - Produkte zum Warenkorb hinzufügen/entfernen
  - Warenkorb-Persistierung (Session-basiert)
  - Warenkorb-Validierung
  - Preisberechnung
- **Endpoints**:
  - `GET /api/cart/{sessionId}` - Warenkorb abrufen
  - `POST /api/cart/{sessionId}/items` - Produkt hinzufügen
  - `DELETE /api/cart/{sessionId}/items/{itemId}` - Produkt entfernen
  - `PUT /api/cart/{sessionId}/items/{itemId}` - Menge aktualisieren
  - `DELETE /api/cart/{sessionId}` - Warenkorb leeren
- **Begründung für Redis**: Schnelle Zugriffe, Session-Management, temporäre Daten

#### Customer Service
- **Zweck**: Kundenverwaltung und Authentifizierung
- **Technologie**: Spring Boot, Spring Security, Spring Data JPA, MySQL
- **Datenbank**: MySQL (eigene DB)
- **Funktionen**:
  - Benutzer-Registrierung und -Login
  - Profil-Verwaltung
  - Adress-Verwaltung
  - Authentifizierung (JWT)
  - Rollen-Management (Customer, Admin)
- **Endpoints**:
  - `POST /api/auth/register` - Registrierung
  - `POST /api/auth/login` - Login
  - `GET /api/customers/{id}` - Kundenprofil
  - `PUT /api/customers/{id}` - Profil aktualisieren
  - `GET /api/customers/{id}/addresses` - Adressen abrufen
  - `POST /api/customers/{id}/addresses` - Adresse hinzufügen
- **Begründung für eigene DB**: Sensible Kundendaten, Compliance (DSGVO), Unabhängigkeit

#### Order Service
- **Zweck**: Bestellverwaltung
- **Technologie**: Spring Boot, Spring Data JPA, MySQL
- **Datenbank**: MySQL (eigene DB)
- **Circuit Breaker**: Resilience4j für Fehlertoleranz bei Payment Service Calls
- **Funktionen**:
  - Bestellung erstellen
  - Bestellstatus verwalten (Pending, Processing, Printing, Shipped, Delivered, Cancelled)
  - Bestellhistorie
  - Integration mit Payment Service (mit Circuit Breaker)
  - Kafka Events: `order-created`, `order-status-updated`
- **Endpoints**:
  - `POST /api/orders` - Neue Bestellung erstellen
  - `GET /api/orders/{id}` - Bestelldetails
  - `GET /api/orders/customer/{customerId}` - Bestellhistorie
  - `PUT /api/orders/{id}/status` - Status aktualisieren
- **Begründung für eigene DB**: Bestelldaten sind kritisch für Business-Operations, Audit-Trail notwendig

#### Payment Service (Mockup)
- **Zweck**: Zahlungsverarbeitung (Mockup-Implementierung)
- **Technologie**: Spring Boot
- **Datenbank**: In-Memory (Mockup)
- **Funktionen**:
  - Zahlung simulieren
  - Zahlungsstatus verwalten
  - Verschiedene Zahlungsmethoden simulieren (Credit Card, PayPal)
  - Kafka Event: `payment-processed`
- **Endpoints**:
  - `POST /api/payments` - Zahlung durchführen
  - `GET /api/payments/{id}` - Zahlungsstatus
  - `POST /api/payments/{id}/refund` - Rückerstattung (Mockup)
- **Begründung für Mockup**: Keine echten Zahlungsdaten in Testumgebung

#### Manufacturing Service
- **Zweck**: Verwaltung der 3D-Druck-Jobs
- **Technologie**: Spring Boot, Spring Data JPA, MySQL
- **Datenbank**: MySQL (eigene DB) ODER Mockup
- **Funktionen**:
  - Print-Job Erstellung nach Bestellung
  - Print-Queue Management
  - Status-Tracking (Queued, Printing, Completed, Failed)
  - Material-Verbrauch-Tracking
  - Kafka Consumer: `order-created`, `payment-processed`
  - Kafka Producer: `manufacturing-job-created`, `inventory-updated`
- **Endpoints**:
  - `POST /api/manufacturing/jobs` - Print-Job erstellen
  - `GET /api/manufacturing/jobs/{id}` - Job-Status
  - `GET /api/manufacturing/queue` - Print-Queue abrufen
  - `PUT /api/manufacturing/jobs/{id}/status` - Status aktualisieren
- **Begründung**: Kann mit Mockup arbeiten, da es primär um Architektur-Demonstration geht

#### Review Service
- **Zweck**: Produktbewertungen und Rezensionen
- **Technologie**: Spring Boot, Spring Data JPA, MySQL
- **Datenbank**: MySQL (eigene DB) ODER Mockup
- **Funktionen**:
  - Bewertungen erstellen (1-5 Sterne)
  - Rezensionen schreiben
  - Bewertungen abrufen (sortiert nach Datum, Rating)
  - Durchschnittsbewertung berechnen
- **Endpoints**:
  - `POST /api/reviews` - Bewertung hinzufügen
  - `GET /api/reviews/product/{productId}` - Bewertungen für Produkt
  - `GET /api/reviews/product/{productId}/average` - Durchschnittsbewertung
- **Begründung**: Kann mit Mockup arbeiten

### 3.3 Frontend

#### React Frontend Application
- **Technologie**: React, TypeScript, shadcn/ui (UI Components)
- **Funktionen**:
  - Produktkatalog-Browsing
  - Warenkorb-Verwaltung
  - Bestellprozess
  - Kundenprofil
  - Bewertungen anzeigen
- **Kommunikation**: REST API über API Gateway

## 4. Datenbank-Strategie

### 4.1 Datenbank-Per-Service Prinzip
Jeder Service hat seine eigene Datenbank, um:
- **Unabhängigkeit**: Services können unabhängig deployed werden
- **Skalierbarkeit**: Jede DB kann separat skaliert werden
- **Technologie-Flexibilität**: Verschiedene DB-Typen je nach Anforderung

### 4.2 Datenbank-Zuordnung

| Service | Datenbank | Typ | Begründung |
|---------|-----------|-----|------------|
| Product Catalog | MySQL | Relational | Produktdaten mit Beziehungen (Kategorien, Varianten) |
| Cart | Redis | NoSQL | Schnelle Zugriffe, Session-Management, temporäre Daten |
| Customer | MySQL | Relational | Strukturierte Kundendaten, Compliance |
| Order | MySQL | Relational | Transaktionale Daten, Audit-Trail |
| Payment | In-Memory | Mockup | Test-Umgebung, keine echten Daten |
| Manufacturing | MySQL/Mockup | Relational/Mockup | Print-Job Tracking (kann Mockup sein) |
| Review | MySQL/Mockup | Relational/Mockup | Bewertungsdaten (kann Mockup sein) |

### 4.3 Docker-Container für Datenbanken
Alle Datenbanken werden via Docker Compose gestartet:
- MySQL Container für relationale Datenbanken
- Redis Container für Cart Service
- Kafka + Zookeeper Container für Messaging

## 5. Sicherheitsaspekte

### 5.1 Authentifizierung & Authorization
- JWT-basierte Authentifizierung (Customer Service)
- API Gateway validiert Tokens
- Rollen-basierte Zugriffskontrolle (RBAC)

### 5.2 Datenübertragung
- HTTPS für alle externen Kommunikationen
- Sensible Daten (Passwörter, Zahlungsinformationen) werden verschlüsselt übertragen
- API Gateway als Security-Layer

### 5.3 Datenvalidierung
- Input-Validierung auf allen Endpoints
- SQL-Injection Prevention (Spring Data JPA)
- XSS Prevention

## 6. Fehlerbehandlung & Resilience

### 6.1 Circuit Breaker Pattern
- **Resilience4j** im Order Service für Payment Service Calls
- Fallback-Mechanismen bei Service-Ausfällen
- Retry-Logik für transient failures

### 6.2 Error Handling
- Zentrale Exception-Handling
- Strukturierte Error-Responses
- Logging und Monitoring

### 6.3 Health Checks
- Spring Boot Actuator für Health Endpoints
- Eureka Health Checks
- Database Connection Monitoring

## 7. API-Dokumentation

### 7.1 Swagger/OpenAPI
- Jeder Service dokumentiert seine APIs mit Swagger
- Zentrale API-Dokumentation über API Gateway
- Interactive API Testing über Swagger UI

## 8. Deployment & CI/CD

### 8.1 GitHub Actions Pipeline
- **Stages**: Build → Test → Deploy
- Automatisches Testing bei jedem Push
- Docker Image Build mit Versionierung
- Deployment zu Cloud-Plattform (Fly.io, AWS, Azure)

### 8.2 Docker Containerization
- Jeder Service als Docker Container
- Docker Compose für lokale Entwicklung
- Multi-stage Docker Builds für optimierte Images

## 9. Projektstruktur

```
tcg-accessories-shop/
├── services/
│   ├── api-gateway/
│   ├── eureka-server/
│   ├── product-catalog-service/
│   ├── cart-service/
│   ├── customer-service/
│   ├── order-service/
│   ├── payment-service/
│   ├── manufacturing-service/
│   └── review-service/
├── frontend/
│   └── tcg-shop-frontend/
├── docker/
│   └── docker-compose.yml
├── .github/
│   └── workflows/
│       └── ci-cd.yml
├── docs/
│   ├── PROJEKTDEFINITION.md (dieses Dokument)
│   ├── ARCHITEKTUR.md
│   └── REFLEXION.md
└── README.md
```

## 10. Nächste Schritte

1. ✅ Projektdefinition (dieses Dokument)
2. ⏳ Grafische Architekturskizze erstellen
3. ⏳ Git-Repository einrichten
4. ⏳ Initiale Service-Struktur aufsetzen
5. ⏳ Docker Compose für lokale Entwicklung
6. ⏳ CI/CD Pipeline konfigurieren
7. ⏳ Erste Service-Implementierungen
8. ⏳ Frontend-Grundgerüst
9. ⏳ Integration und Testing
10. ⏳ Dokumentation vervollständigen

