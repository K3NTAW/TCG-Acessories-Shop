# Projektstruktur

## Verzeichnisstruktur

```
tcg-accessories-shop/
│
├── .github/
│   └── workflows/
│       └── ci-cd.yml                    # GitHub Actions CI/CD Pipeline
│
├── docker/
│   └── docker-compose.yml               # Docker Compose für lokale Entwicklung
│
├── docs/
│   ├── ARCHITEKTUR.md                  # Detaillierte Architektur-Dokumentation
│   ├── ARCHITEKTURSKIZZE.md            # Grafische Architekturskizze (Text-basiert)
│   └── REFLEXION.md                    # Projekt-Reflexion
│
├── services/
│   ├── api-gateway/                    # Spring Cloud Gateway
│   │   ├── src/
│   │   │   └── main/
│   │   │       ├── java/
│   │   │       │   └── com/tcgshop/gateway/
│   │   │       └── resources/
│   │   │           └── application.yml
│   │   ├── Dockerfile
│   │   └── pom.xml
│   │
│   ├── eureka-server/                  # Eureka Service Discovery
│   │   ├── src/
│   │   │   └── main/
│   │   │       ├── java/
│   │   │       │   └── com/tcgshop/eureka/
│   │   │       └── resources/
│   │   │           └── application.yml
│   │   ├── Dockerfile
│   │   └── pom.xml
│   │
│   ├── product-catalog-service/        # Produktkatalog Service
│   │   ├── src/
│   │   │   └── main/
│   │   │       ├── java/
│   │   │       │   └── com/tcgshop/product/
│   │   │       │       ├── controller/
│   │   │       │       ├── service/
│   │   │       │       ├── repository/
│   │   │       │       ├── model/
│   │   │       │       └── ProductCatalogApplication.java
│   │   │       └── resources/
│   │   │           ├── application.yml
│   │   │           └── db/migration/   # Flyway/Liquibase Migrations
│   │   ├── Dockerfile
│   │   └── pom.xml
│   │
│   ├── cart-service/                   # Warenkorb Service
│   │   ├── src/
│   │   │   └── main/
│   │   │       ├── java/
│   │   │       │   └── com/tcgshop/cart/
│   │   │       └── resources/
│   │   │           └── application.yml
│   │   ├── Dockerfile
│   │   └── pom.xml
│   │
│   ├── customer-service/               # Kundenverwaltung Service
│   │   ├── src/
│   │   │   └── main/
│   │   │       ├── java/
│   │   │       │   └── com/tcgshop/customer/
│   │   │       └── resources/
│   │   │           └── application.yml
│   │   ├── Dockerfile
│   │   └── pom.xml
│   │
│   ├── order-service/                  # Bestellverwaltung Service
│   │   ├── src/
│   │   │   └── main/
│   │   │       ├── java/
│   │   │       │   └── com/tcgshop/order/
│   │   │       └── resources/
│   │   │           └── application.yml
│   │   ├── Dockerfile
│   │   └── pom.xml
│   │
│   ├── payment-service/                # Zahlungsverarbeitung Service (Mockup)
│   │   ├── src/
│   │   │   └── main/
│   │   │       ├── java/
│   │   │       │   └── com/tcgshop/payment/
│   │   │       └── resources/
│   │   │           └── application.yml
│   │   ├── Dockerfile
│   │   └── pom.xml
│   │
│   ├── manufacturing-service/         # 3D-Druck-Job Verwaltung
│   │   ├── src/
│   │   │   └── main/
│   │   │       ├── java/
│   │   │       │   └── com/tcgshop/manufacturing/
│   │   │       └── resources/
│   │   │           └── application.yml
│   │   ├── Dockerfile
│   │   └── pom.xml
│   │
│   └── review-service/                 # Produktbewertungen Service
│       ├── src/
│       │   └── main/
│       │       ├── java/
│       │       │   └── com/tcgshop/review/
│       │       └── resources/
│       │           └── application.yml
│       ├── Dockerfile
│       └── pom.xml
│
├── frontend/
│   └── tcg-shop-frontend/              # React Frontend
│       ├── src/
│       │   ├── components/
│       │   ├── pages/
│       │   ├── services/
│       │   ├── hooks/
│       │   └── App.tsx
│       ├── public/
│       ├── package.json
│       ├── tsconfig.json
│       └── Dockerfile
│
├── tests/
│   └── postman/
│       ├── collection.json             # Postman Collection für API Tests
│       └── environment.json            # Postman Environment
│
├── .gitignore
├── README.md                           # Haupt-README
├── PROJEKTDEFINITION.md                # Projektdefinition
├── PROJEKTSTRUKTUR.md                  # Diese Datei
└── LICENSE
```

## Service-Struktur (Beispiel: Product Catalog Service)

```
product-catalog-service/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── tcgshop/
│       │           └── product/
│       │               ├── ProductCatalogApplication.java
│       │               ├── controller/
│       │               │   └── ProductController.java
│       │               ├── service/
│       │               │   └── ProductService.java
│       │               ├── repository/
│       │               │   └── ProductRepository.java
│       │               ├── model/
│       │               │   ├── Product.java
│       │               │   └── ProductVariant.java
│       │               ├── dto/
│       │               │   ├── ProductDTO.java
│       │               │   └── CreateProductRequest.java
│       │               └── config/
│       │                   ├── SwaggerConfig.java
│       │                   └── DatabaseConfig.java
│       └── resources/
│           ├── application.yml
│           ├── application-dev.yml
│           ├── application-prod.yml
│           └── db/
│               └── migration/
│                   └── V1__init_schema.sql
├── src/
│   └── test/
│       └── java/
│           └── com/
│               └── tcgshop/
│                   └── product/
│                       ├── controller/
│                       │   └── ProductControllerTest.java
│                       ├── service/
│                       │   └── ProductServiceTest.java
│                       └── integration/
│                           └── ProductIntegrationTest.java
├── Dockerfile
└── pom.xml
```

## Docker-Struktur

Jeder Service hat ein eigenes `Dockerfile`:

```dockerfile
# Beispiel Dockerfile
FROM maven:3.8.7-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM openjdk:17-jre-slim
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
```

## Konfigurations-Dateien

### application.yml (Beispiel: Product Catalog Service)
```yaml
server:
  port: 8081

spring:
  application:
    name: product-catalog-service
  datasource:
    url: jdbc:mysql://mysql-product-catalog:3306/product_catalog_db
    username: appuser
    password: apppassword
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: true

eureka:
  client:
    service-url:
      defaultZone: http://eureka-server:8761/eureka/

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
```

## Build & Deployment

### Lokale Entwicklung
```bash
# Docker Services starten
docker-compose up -d

# Services einzeln starten (in IntelliJ IDEA oder via Maven)
mvn spring-boot:run
```

### CI/CD Pipeline
- Automatischer Build bei jedem Push
- Tests werden ausgeführt
- Docker Images werden gebaut und zu Registry gepusht
- Deployment zu Cloud-Plattform

## Naming Conventions

- **Packages**: `com.tcgshop.{service}`
- **Classes**: PascalCase (z.B. `ProductController`)
- **Methods**: camelCase (z.B. `getProductById`)
- **Constants**: UPPER_SNAKE_CASE (z.B. `MAX_RETRY_COUNT`)
- **Database Tables**: snake_case (z.B. `order_items`)

## Testing-Struktur

- **Unit Tests**: `src/test/java/.../service/`, `.../controller/`
- **Integration Tests**: `src/test/java/.../integration/`
- **API Tests**: `tests/postman/collection.json`

