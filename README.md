# SmartTrade - Energy Trade DB (Java / Spring Boot)

Java code and SQL for the six entities in the ER diagram: Energy Order, Energy Trade,
GridZone Context, MeterReading, User, SmartMeter. Package `com.TradeP.SmartTrade`,
same layout as the repo, so the folders copy straight over it.

## What is inside

| File | What it is |
|---|---|
| `entity/EnergyTrade.java` | new (was empty). Two FKs to `EnergyOrder`, cleared qty / price, total cost, `executed_at` |
| `entity/SmartMeter.java` | replaced. Now `user_id` FK + `zone_id` FK + lat/long, as in the diagram |
| `entity/MeterReading.java` | replaced. Now generation, consumption, net grid flow, battery SoC, as in the diagram |
| `repository/EnergyTradeRepository.java` | new (was an empty class, now a real `JpaRepository`) |
| `repository/EnergyOrderRepository.java` | adds row-locking finder + a few queries |
| `repository/UserRepository.java` | adds row-locking finder (used to move wallet money safely) |
| `repository/SmartMeterRepository.java`, `MeterReadingRepository.java` | replaced to match the new entities |
| `service/EnergyTradeService.java` | executes a trade in one DB transaction (see below) |
| `controller/EnergyTradeController.java` | `POST /api/trades`, `GET /api/trades`, `/{id}`, `/user/{userId}`, `/order/{orderId}` |
| `controller/MeterReadingController.java` | `POST /api/readings`, `GET /{id}`, `/meter/{meterId}` (optional `?from=&to=`), `/meter/{meterId}/latest` |
| `controller/SmartMeterController.java` | replaced. CRUD on `/api/meters` plus `/user/{id}` and `/zone/{id}` |
| `dto/*` | request/response records, so JSON never exposes lazy entities or user data |
| `docs/energy_trade_schema.sql` | all six tables with FKs, CHECK constraints and indexes (PostgreSQL) |
| `docs/energy_trade_seed.sql` | two users, two meters, a SELL and a BUY order to test with |

Already matching the diagram, so **not** in this zip: `User`, `GridZone`, `EnergyOrder`
(it also has `remaining_quantity_kwh` and `expires_at`, which are kept because partial fills need them).

## How to apply

1. In the repo, rename `controller/EneryOrderController.java` to `EnergyOrderController.java`
   (the class inside is already called `EnergyOrderController`; Java needs the file name to match).
2. Copy `src/` and `docs/` from this zip into the repo root and accept the overwrites.
3. If your local database already has the old `smart_meter` / `meter_reading` tables, drop them first
   (dev only; the commented RESET block at the top of `energy_trade_schema.sql` does it).
   `ddl-auto=update` adds columns but never removes the old NOT NULL ones, so inserts would fail.
4. Run the app. Optionally run `docs/grid_zone.sql` then `docs/energy_trade_seed.sql` for test data.

## Try a trade (with the seed data)

```
POST /api/trades
{
  "buyOrderId":  "cccccccc-0000-0000-0000-000000000002",
  "sellOrderId": "cccccccc-0000-0000-0000-000000000001",
  "clearedQuantityKwh": 4
}
```
Expected: price 6.0000 (the older order sets it), total cost 24.0000, buy order `FILLED`,
sell order `PARTIALLY_FILLED` with 6 kWh left, buyer wallet 500 -> 476, seller 0 -> 24.

## How a trade works

`EnergyTradeService.executeTrade` is a single transaction: it locks both orders (and both
wallets), checks the rules, then writes the `energy_trade` row, updates both orders, moves the money
and writes a DEBIT and a CREDIT `wallet_transaction`. If any check fails, nothing is written.

Rules I chose (change them in the service if your design differs):
- `buyOrderId` must be a BUY order and `sellOrderId` a SELL order; buyer and seller must be different users.
- Both orders must be `OPEN` or `PARTIALLY_FILLED` and not expired. Status flow: OPEN -> PARTIALLY_FILLED -> FILLED.
- Quantity cannot exceed either order's remaining kWh.
- Cleared price must be between the seller's ask and the buyer's limit. If you omit it, the older order's price is used.
- `total_cost = quantity x price`, rounded to 4 decimals. The buyer's wallet must cover it.
- Trades and meter readings have no update/delete endpoints (ledger-style).
- Orders in different grid zones can still trade; no zone check yet.
- Meter reading `net_grid_flow_kwh` defaults to generation - consumption (positive = surplus to sell).
