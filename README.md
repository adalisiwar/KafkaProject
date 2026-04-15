# Kafka Order Management System

Full-stack e-commerce order management demo built with Spring Boot 3, React 18, PostgreSQL 15, and Apache Kafka in KRaft mode.

## What it demonstrates

- Order pipeline orchestration across Kafka topics: `order.created`, `order.validated`, `order.rejected`, `order.paid`, `order.shipped`, and `notification.email`
- Fan-out consumption on `order.paid` by both order status processing and analytics
- Manual acknowledgment for Kafka consumers
- Dead-letter publishing to `order.dlq`
- Real-time frontend polling every 3 seconds
- HTML email notifications through MailDev

## Project structure

```text
backend/   Spring Boot API + Kafka producers/consumers + PostgreSQL persistence
frontend/  React + Vite + Tailwind dashboard
docker-compose.yml
README.md
```

## Prerequisites

- Docker Desktop
- Java 17 and Maven if you want to run the backend outside Docker
- Node.js 20+ if you want to run the frontend outside Docker

## Start everything with Docker Compose

1. Open a terminal in the project root.
2. Run:

```bash
docker compose up --build
```

3. Wait for all containers to become healthy.

## Service URLs

- Frontend: [http://localhost:3000](http://localhost:3000)
- Backend API: [http://localhost:8081](http://localhost:8081)
- Kafka UI: [http://localhost:8080](http://localhost:8080)
- MailDev UI: [http://localhost:1080](http://localhost:1080)
- PostgreSQL: `localhost:5432`

## Local development

### Backend

```bash
cd backend
mvn spring-boot:run
```

Environment defaults are already set in `backend/src/main/resources/application.yml`.

### Frontend

```bash
cd frontend
npm install
npm run dev
```

## API overview

- `POST /api/orders` place a new order
- `GET /api/orders` list all orders
- `GET /api/orders/{id}` get order details
- `POST /api/orders/{id}/pay` simulate payment
- `GET /api/products` list products
- `POST /api/products` create a product
- `GET /api/customers` list customers
- `POST /api/customers` create a customer
- `GET /api/analytics/daily` get in-memory paid order summary
- `GET /api/notifications/{orderId}` get notification history

## Kafka flow

See the diagrams below. View them individually:

1. Placing an order saves the order in PostgreSQL and publishes `order.created`.
2. `InventoryConsumer` validates stock:
   If stock is available, it reserves stock and publishes `order.validated`.
   If stock is insufficient, it publishes `order.rejected`.
3. `OrderStatusConsumer` updates order state in PostgreSQL and emits `notification.email`.
4. Payment is simulated through `POST /api/orders/{id}/pay`, which publishes `order.paid` on success.
5. `order.paid` is consumed by both `OrderStatusConsumer` (updates status to PAID, publishes shipping and notifications) and `AnalyticsConsumer` (updates metrics).
6. Shipping publishes `order.shipped`, which updates the order and triggers another email notification.

## Architecture Diagrams

### 1. Order Lifecycle Sequence

```mermaid
sequenceDiagram
    participant F as Frontend
    participant API as OrderController
    participant OS as OrderService
    participant OP as OrderProducer
    participant IC as InventoryConsumer
    participant OSC as OrderStatusConsumer
    participant AC as AnalyticsConsumer
    participant PP as PaymentProducer
    participant NC as NotificationConsumer
    participant DB as PostgreSQL
    participant Email as MailDev
    
    F->>API: POST /api/orders
    API->>OS: createOrder()
    OS->>DB: save order
    OS->>OP: publish order.created
    OP->>IC: order.created
    IC->>DB: reserve stock
    IC->>OSC: order.validated
    Note over OSC,AC: Later: simulate payment
    API->>PP: POST /api/orders/{id}/pay
    PP->>OSC: order.paid
    PP->>AC: order.paid (fan-out)
    OSC->>DB: update status=PAID
    OSC->>NC: notification.email
    NC->>Email: send email
    AC->>DB: update analytics
```

### 2. Kafka Topics & Fan-out

```mermaid
graph LR
    OP[OrderProducer<br/>order.created] --> IC[InventoryConsumer]
    IC --> OV[StatusProducer<br/>order.validated]
    IC -.->|reject| OR[StatusProducer<br/>order.rejected]
    PP[PaymentProducer<br/>order.paid] --> OSC
    PP --> AC[AnalyticsConsumer]
    OV --> OSC[OrderStatusConsumer]
    OR --> OSC
    OSC --> NS[StatusProducer<br/>notification.email]
    OSC --> SP[ShippingProducer<br/>order.shipped]
    NS --> NC[NotificationConsumer]
    SP --> OSC
    
    classDef producer fill:#90EE90
    classDef consumer fill:#ADD8E6
    class OP,OV,OR,PP,NS,SP producer
    class IC,OSC,AC,NC consumer
```
## Seed data

- 5 customers
- 10 electronics products

These are loaded automatically from `backend/src/main/resources/data.sql`.

## Demo tips

- Use the Place Order page to create a new order.
- Watch order status changes on the Orders page.
- Click Pay once an order becomes `VALIDATED`.
- Open MailDev to inspect the HTML emails sent for each status change.
- Open Kafka UI to inspect topics, partitions, and message payloads.
