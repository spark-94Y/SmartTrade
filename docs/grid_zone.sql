-- GridZone Context table (PostgreSQL)
-- Optional: Hibernate (ddl-auto=update) creates this automatically on startup.
-- Run this only if you want to create/seed the table manually in pgAdmin.

CREATE TABLE IF NOT EXISTS grid_zone (
    zone_id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    zone_name                    VARCHAR(100)   NOT NULL UNIQUE,
    current_solar_irradiance     NUMERIC(8,2)   NOT NULL DEFAULT 0,
    weather_condition            VARCHAR(50)    DEFAULT 'UNKNOWN',
    total_aggregate_reserve_kwh  NUMERIC(14,4)  NOT NULL DEFAULT 0,
    grid_status                  VARCHAR(30)    NOT NULL DEFAULT 'STABLE',
    base_market_clearing_price   NUMERIC(12,4)  NOT NULL DEFAULT 0,
    CONSTRAINT chk_irradiance_nonneg CHECK (current_solar_irradiance >= 0),
    CONSTRAINT chk_reserve_nonneg    CHECK (total_aggregate_reserve_kwh >= 0),
    CONSTRAINT chk_price_nonneg      CHECK (base_market_clearing_price >= 0)
);

-- Sample data
INSERT INTO grid_zone
    (zone_name, current_solar_irradiance, weather_condition,
     total_aggregate_reserve_kwh, grid_status, base_market_clearing_price)
VALUES
    ('Andheri West', 780.50, 'CLEAR',  1250.7500, 'STABLE',    6.5000),
    ('Bandra East',  310.00, 'CLOUDY',  940.2000, 'CONGESTED', 7.2500),
    ('Powai',          0.00, 'CLEAR',   610.0000, 'STABLE',    6.0000)
ON CONFLICT (zone_name) DO NOTHING;
