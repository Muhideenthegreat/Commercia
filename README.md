# Commercia

Full-stack e-commerce platform built with Java, Spring Boot, Spring Security, JPA, JWT, and MySQL/PostgreSQL.

## Features
- JWT-based authentication and role-based authorization (USER/ADMIN)
- Product catalog with categories and rich seeded data
- Cart and checkout flows with order history
- REST APIs + a lightweight storefront UI (static HTML/JS)

## Quick Start (MySQL)
1) Start the database:
```bash
cd /Users/muhideen/Documents/Projects/Commercia
docker compose up -d mysql
```

2) Run the API:
```bash
COMMERCIA_JWT_SECRET=change-me mvn spring-boot:run -Dspring-boot.run.profiles=mysql
```

3) Open the UI:
- http://localhost:8080/

## Quick Start (PostgreSQL)
```bash
cd /Users/muhideen/Documents/Projects/Commercia
docker compose up -d postgres
COMMERCIA_JWT_SECRET=change-me mvn spring-boot:run -Dspring-boot.run.profiles=postgres
```

## Default Accounts (demo only)
- Admin: `admin@commercia.local` / `admin123`

## Environment Variables
- `COMMERCIA_JWT_SECRET`: required, strong random string for JWT signing

## API Overview
- `POST /api/auth/register`
- `POST /api/auth/login`
- `GET /api/catalog/categories`
- `GET /api/catalog/products`
- `GET /api/catalog/products/{id}`
- `GET /api/cart`
- `POST /api/cart/items`
- `PATCH /api/cart/items/{itemId}`
- `DELETE /api/cart/items/{itemId}`
- `POST /api/orders`
- `GET /api/orders`

## Build
```bash
mvn clean package
```

## AWS Notes (not deployed)
This project is ready to deploy on AWS (e.g., ECS + RDS). For a simple path:
- Use RDS for MySQL/PostgreSQL
- Set `spring.profiles.active` to the DB you choose
- Provide `app.jwt.secret` via environment variable
