# URL Shortener API

A REST API for shortening URLs with click tracking and expiry support.

## Tech stack

- Java 21
- Spring Boot
- PostgreSQL
- Docker & Docker Compose
- GitHub Actions CI

## Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | '/api/shorten' | Shorten an URL |
| GET | '/{shortCode}' | Redirect to an URL associated with the shortCode |
| GET | '/api/stats/{shortCode}' | See statistics for a specific shortCode |

## Running locally

**Prerequisites:** Docker

```bash
curl -X POST http://localhost:8080/api/shorten -H "Content-Type: application/json" -d '{"originalUrl": "https://google.com", "daysUntilExpiry": 7}'
```

```bash
curl http://localhost:8080/api/stats/{shortCode}
```

## CI

Every push to the `main` branch triggers a GitHub Actions pipeline that builds the project and Docker image automatically.