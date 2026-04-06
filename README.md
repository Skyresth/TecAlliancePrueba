# Pricing API

Pricing service for shop articles with date-based discounts, VAT calculation, floor protection against selling below cost, local caching, rate limiting, Flyway migrations, and Actuator metrics.

## Stack

- Java 21
- Spring Boot 3.3
- Maven
- PostgreSQL
- Flyway
- Caffeine
- Bucket4j
- Springdoc Swagger UI

## Business semantics

- `costPriceExclVat` is the article floor. Discounted sale price never goes below it.
- `baseSalePriceExclVat` is the normal customer-facing sale price before discount.
- `vatRate` is a ratio between `0` and `1`. Example: `0.21` means 21% VAT.
- Percentage discounts also use a ratio between `0` and `1`.
- Discount dates are inclusive.
- Only enabled discounts are considered for pricing.
- Overlapping enabled discounts for the same article are rejected at write time.

## Run locally

```bash
./mvnw spring-boot:run
```

The default datasource points to PostgreSQL on `localhost:5432`. For a full local stack:

```bash
docker compose up --build
```

## Main endpoints

- `POST /api/v1/articles`
- `PUT /api/v1/articles/{articleId}`
- `GET /api/v1/articles/{articleId}`
- `POST /api/v1/articles/{articleId}/discounts`
- `GET /api/v1/articles/{articleId}/price?effectiveAt=2021-03-15`
- `GET /api/v1/articles?effectiveAt=2021-03-15&page=0&size=20`

Swagger UI is available at `/swagger-ui.html`.

## Observability

- Health: `/actuator/health`
- Liveness: `/actuator/health/liveness`
- Readiness: `/actuator/health/readiness`
- Prometheus: `/actuator/prometheus`
- Cache metrics: `/actuator/metrics/cache.gets`

## Tests

```bash
./mvnw test
```
