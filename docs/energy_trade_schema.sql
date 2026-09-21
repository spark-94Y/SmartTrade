-- =====================================================================
-- SmartTrade - Energy Trade database schema (PostgreSQL 13+)
-- Matches the ER diagram: Energy Order, Energy Trade, GridZone Context,
-- MeterReading, User, SmartMeter.
--
-- OPTIONAL: Hibernate (spring.jpa.hibernate.ddl-auto=update) creates these
-- tables on startup. Run this file in pgAdmin only if you want the CHECK
-- constraints and indexes below, or a clean manual setup.
--
-- The wallet_transaction and house tables are not in the diagram and are
-- not created here; Hibernate keeps creating them from their entities.
-- =====================================================================

-- ---------------------------------------------------------------------
-- RESET (dev only, destroys data). Uncomment if your database still has
-- the OLD smart_meter / meter_reading tables (house_id, meter_number,
-- reading_value, reading_type columns). ddl-auto=update never removes
-- old columns, and their NOT NULL rules would make inserts fail.
-- ---------------------------------------------------------------------
-- DROP TABLE IF EXISTS energy_trade  CASCADE;
-- DROP TABLE IF EXISTS energy_order  CASCADE;
-- DROP TABLE IF EXISTS meter_reading CASCADE;
-- DROP TABLE IF EXISTS smart_meter   CASCADE;

-- ---------------------------------------------------------------------
-- User Entity
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS "user" (
    user_id        UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    full_name      VARCHAR(255),
    email          VARCHAR(255)  NOT NULL UNIQUE,
    password_hash  VARCHAR(255),
    wallet_balance NUMERIC(12,4) NOT NULL DEFAULT 0,
    role           VARCHAR(255),
    created_at     TIMESTAMP(6)  NOT NULL DEFAULT now(),
    CONSTRAINT chk_user_wallet_nonneg CHECK (wallet_balance >= 0)
);

-- ---------------------------------------------------------------------
-- GridZone Context (same as docs/grid_zone.sql)
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS grid_zone (
    zone_id                      UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    zone_name                    VARCHAR(100)  NOT NULL UNIQUE,
    current_solar_irradiance     NUMERIC(8,2)  NOT NULL DEFAULT 0,
    weather_condition            VARCHAR(50)   DEFAULT 'UNKNOWN',
    total_aggregate_reserve_kwh  NUMERIC(14,4) NOT NULL DEFAULT 0,
    grid_status                  VARCHAR(30)   NOT NULL DEFAULT 'STABLE',
    base_market_clearing_price   NUMERIC(12,4) NOT NULL DEFAULT 0,
    CONSTRAINT chk_irradiance_nonneg CHECK (current_solar_irradiance >= 0),
    CONSTRAINT chk_reserve_nonneg    CHECK (total_aggregate_reserve_kwh >= 0),
    CONSTRAINT chk_price_nonneg      CHECK (base_market_clearing_price >= 0)
);

-- ---------------------------------------------------------------------
-- SmartMeter Entity
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS smart_meter (
    meter_id         UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id          UUID         NOT NULL REFERENCES "user"(user_id),
    zone_id          UUID         NOT NULL REFERENCES grid_zone(zone_id),
    hardware_version VARCHAR(50),
    firmware_version VARCHAR(50),
    latitude         NUMERIC(9,6),
    longitude        NUMERIC(9,6),
    status           VARCHAR(30)  NOT NULL DEFAULT 'ONLINE',
    CONSTRAINT chk_meter_lat CHECK (latitude  BETWEEN -90  AND 90),
    CONSTRAINT chk_meter_lon CHECK (longitude BETWEEN -180 AND 180)
);
CREATE INDEX IF NOT EXISTS idx_smart_meter_user ON smart_meter (user_id);
CREATE INDEX IF NOT EXISTS idx_smart_meter_zone ON smart_meter (zone_id);

-- ---------------------------------------------------------------------
-- MeterReading Entity (append-only)
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS meter_reading (
    reading_id              UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    meter_id                UUID          NOT NULL REFERENCES smart_meter(meter_id),
    "timestamp"             TIMESTAMP(6)  NOT NULL DEFAULT now(),
    current_generation_kwh  NUMERIC(12,4) NOT NULL DEFAULT 0,
    current_consumption_kwh NUMERIC(12,4) NOT NULL DEFAULT 0,
    net_grid_flow_kwh       NUMERIC(12,4) NOT NULL DEFAULT 0,   -- >0 exporting, <0 importing
    battery_soc_percentage  NUMERIC(5,2),                       -- NULL = no battery
    CONSTRAINT chk_reading_generation_nonneg  CHECK (current_generation_kwh  >= 0),
    CONSTRAINT chk_reading_consumption_nonneg CHECK (current_consumption_kwh >= 0),
    CONSTRAINT chk_reading_soc_range          CHECK (battery_soc_percentage BETWEEN 0 AND 100)
);
CREATE INDEX IF NOT EXISTS idx_meter_reading_meter_time ON meter_reading (meter_id, "timestamp" DESC);

-- ---------------------------------------------------------------------
-- Energy Order
-- (remaining_quantity_kwh and expires_at are not in the diagram; they were
--  already in the EnergyOrder entity and are needed for partial fills)
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS energy_order (
    order_id               UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id                UUID          NOT NULL REFERENCES "user"(user_id),
    meter_id               UUID          NOT NULL REFERENCES smart_meter(meter_id),
    order_type             VARCHAR(10)   NOT NULL,               -- BUY or SELL
    energy_quantity_kwh    NUMERIC(10,4) NOT NULL,
    remaining_quantity_kwh NUMERIC(10,4) NOT NULL,
    price_per_kwh          NUMERIC(10,4) NOT NULL,
    status                 VARCHAR(30)   NOT NULL DEFAULT 'OPEN', -- OPEN, PARTIALLY_FILLED, FILLED, CANCELLED
    created_at             TIMESTAMP(6)  NOT NULL DEFAULT now(),
    expires_at             TIMESTAMP(6),
    CONSTRAINT chk_order_type      CHECK (UPPER(order_type) IN ('BUY', 'SELL')),
    CONSTRAINT chk_order_quantity  CHECK (energy_quantity_kwh > 0),
    CONSTRAINT chk_order_remaining CHECK (remaining_quantity_kwh >= 0 AND remaining_quantity_kwh <= energy_quantity_kwh),
    CONSTRAINT chk_order_price     CHECK (price_per_kwh > 0)
);
CREATE INDEX IF NOT EXISTS idx_energy_order_user  ON energy_order (user_id);
CREATE INDEX IF NOT EXISTS idx_energy_order_meter ON energy_order (meter_id);
CREATE INDEX IF NOT EXISTS idx_energy_order_book  ON energy_order (order_type, status, price_per_kwh);

-- ---------------------------------------------------------------------
-- Energy Trade (immutable ledger: rows are inserted, never updated)
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS energy_trade (
    trade_id              UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    buy_order_id          UUID          NOT NULL REFERENCES energy_order(order_id),
    sell_order_id         UUID          NOT NULL REFERENCES energy_order(order_id),
    cleared_quantity_kwh  NUMERIC(10,4) NOT NULL,
    cleared_price_per_kwh NUMERIC(10,4) NOT NULL,
    total_cost            NUMERIC(14,4) NOT NULL,
    executed_at           TIMESTAMP(6)  NOT NULL DEFAULT now(),
    CONSTRAINT chk_trade_distinct_orders CHECK (buy_order_id <> sell_order_id),
    CONSTRAINT chk_trade_quantity        CHECK (cleared_quantity_kwh > 0),
    CONSTRAINT chk_trade_price           CHECK (cleared_price_per_kwh > 0),
    CONSTRAINT chk_trade_total           CHECK (total_cost >= 0)
);
CREATE INDEX IF NOT EXISTS idx_energy_trade_buy_order  ON energy_trade (buy_order_id);
CREATE INDEX IF NOT EXISTS idx_energy_trade_sell_order ON energy_trade (sell_order_id);
CREATE INDEX IF NOT EXISTS idx_energy_trade_executed   ON energy_trade (executed_at DESC);
