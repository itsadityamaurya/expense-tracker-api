# Expense Tracker API

A clean, production-style **REST API** for tracking personal or business expenses.
Focus: correct layering, validation, pagination/filtering, reporting query, error handling and tests.

## Features
- Full CRUD for expenses (`BigDecimal` money handling, indexed date/category columns)
- Filtering by category and date range, pagination and whitelisted sorting
- Summary report: total spend per category in a date range (JPQL group-by with projection)
- Bean Validation with field-level error messages
- Global exception handler (400 / 404 / 500) with consistent JSON error body
- Swagger UI, H2 by default, MySQL via profile, Docker Compose
- Tests: Mockito service tests + MockMvc integration tests

## Run
```bash
mvn spring-boot:run          # H2, zero setup
docker compose up --build    # app + MySQL
mvn test
```
Swagger UI: http://localhost:8080/swagger-ui.html

## Endpoints
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/expenses` | Create |
| GET | `/api/expenses/{id}` | Get one |
| GET | `/api/expenses?category=FOOD&from=2026-01-01&to=2026-01-31&page=0&size=10&sortBy=amount&direction=desc` | Search |
| PUT | `/api/expenses/{id}` | Update |
| DELETE | `/api/expenses/{id}` | Delete |
| GET | `/api/expenses/summary?from=2026-01-01&to=2026-01-31` | Category totals |

```bash
curl -X POST localhost:8080/api/expenses -H "Content-Type: application/json" \
  -d '{"title":"Lunch","amount":250.50,"category":"FOOD","expenseDate":"2026-01-15","note":"Team lunch"}'
```

## Design notes
- Sort fields are whitelisted, page size is capped at 100 (prevents abuse).
- Filters are built with JPA `Specification`s so adding a new filter is one small block.
- Ideas to extend: user ownership + JWT (see project 1), CSV export, monthly budgets.
