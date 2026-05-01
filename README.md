# ShopForge — Microservices with Orchestration Saga

## Architecture

```
Internet
    │
    ▼
┌─────────────────────────────┐
│   API Gateway  :8000        │  ← Only public port
│   Spring Cloud Gateway      │
│   JWT validation here       │
└────────────┬────────────────┘
             │ routes + X-User-* headers
             ▼
┌─────────────────────────────┐   own DB
│   Order Service  :8081      │──────────── mysql-orders:3306
│   Saga Orchestrator         │                shopforge_orders
│                             │
│   Step 2: HTTP → :8082      │──────────────────────────────────┐
│   Step 3: HTTP → :8083      │────────────────────────┐         │
└─────────────────────────────┘                         │         │
                                                        ▼         ▼
                              ┌────────────────────┐  ┌──────────────────────┐
                              │  Payment Svc :8083  │  │  Inventory Svc :8082 │
                              │  Stripe SDK         │  │  Stock reserve/      │
                              │  own DB             │  │  release + locks     │
                              │  mysql-payments     │  │  own DB              │
                              │  shopforge_payments │  │  mysql-inventory     │
                              └────────────────────┘  └──────────────────────┘
```

## Saga Flow (matches sequence diagram)

1. `POST /api/orders` hits **API Gateway** → JWT validated → forwarded to Order Service
2. Order Service creates order with status `PENDING`
3. **HTTP POST** → `inventory-service:8082/api/inventory/reserve`
   - `[Inventory fails]` → Order cancelled → `400 Out of Stock`
4. **HTTP POST** → `paymentRecord-service:8083/api/payments/charge`
   - `[Payment fails]` → **HTTP POST** → `inventory-service:8082/api/inventory/release` (compensating tx) → Order cancelled → `402 Payment Error`
   - `[Success]` → Order confirmed → `201 Created`

## Quick Start

```bash
# 1. Clone and configure
cp .env.example .env
# Edit .env — add your Stripe keys and a JWT secret

# 2. Copy Dockerfile into each service
cp Dockerfile api-gateway/
cp Dockerfile order-service/
cp Dockerfile inventory-service/
cp Dockerfile paymentRecord-service/

# 3. Start everything
docker-compose up --build

# 4. Test the happy path
curl -X POST http://localhost:8000/api/orders \
  -H "Authorization: Bearer <your_jwt>" \
  -H "Content-Type: application/json" \
  -d '{
    "items": [{"productId": 1, "productName": "Headphones", "quantity": 1, "unitPrice": 49.99}],
    "shippingAddress": {"street":"1 Main St","city":"NY","state":"NY","zipCode":"10001","country":"US"},
    "paymentMethodId": "pm_card_visa"
  }'
```

## Service Ports

| Service           | External Port | Internal Port | Database           |
|-------------------|:---:|:---:|---|
| API Gateway       | 8000 | 8000 | none |
| Order Service     | —    | 8081 | shopforge_orders |
| Inventory Service | —    | 8082 | shopforge_inventory |
| Payment Service   | —    | 8083 | shopforge_payments |
| MySQL (orders)    | 3307 | 3306 | — |
| MySQL (inventory) | 3308 | 3306 | — |
| MySQL (payments)  | 3309 | 3306 | — |

> Ports 8081/8082/8083 are on the internal Docker network only.
> Only port 8000 should be exposed to the internet.
