-- =====================================================================
-- Sample data for trying out POST /api/trades. DEV ONLY.
-- Run docs/grid_zone.sql first (it creates the 'Andheri West' zone), and
-- run this after the tables exist (start the app once, or run
-- energy_trade_schema.sql).
--
-- Result: a seller with a 10 kWh SELL order at 6.00, and a buyer with a
-- 4 kWh BUY order at 8.00 and a 500.00 wallet.
-- password_hash is a placeholder, so these two users cannot log in.
-- =====================================================================

INSERT INTO "user" (user_id, full_name, email, password_hash, wallet_balance, role)
VALUES
  ('aaaaaaaa-0000-0000-0000-000000000001', 'Sample Seller', 'seller@example.com', 'x', 0.0000,   'PROSUMER'),
  ('aaaaaaaa-0000-0000-0000-000000000002', 'Sample Buyer',  'buyer@example.com',  'x', 500.0000, 'CONSUMER')
ON CONFLICT DO NOTHING;

INSERT INTO smart_meter (meter_id, user_id, zone_id, hardware_version, firmware_version, latitude, longitude, status)
VALUES
  ('bbbbbbbb-0000-0000-0000-000000000001', 'aaaaaaaa-0000-0000-0000-000000000001',
   (SELECT zone_id FROM grid_zone WHERE zone_name = 'Andheri West'), 'HW-2.1', 'FW-1.4.0', 19.136300, 72.825100, 'ONLINE'),
  ('bbbbbbbb-0000-0000-0000-000000000002', 'aaaaaaaa-0000-0000-0000-000000000002',
   (SELECT zone_id FROM grid_zone WHERE zone_name = 'Andheri West'), 'HW-2.1', 'FW-1.4.0', 19.119700, 72.846400, 'ONLINE')
ON CONFLICT DO NOTHING;

INSERT INTO meter_reading (meter_id, "timestamp", current_generation_kwh, current_consumption_kwh, net_grid_flow_kwh, battery_soc_percentage)
VALUES
  ('bbbbbbbb-0000-0000-0000-000000000001', now(), 12.5000, 2.5000, 10.0000, 85.00),
  ('bbbbbbbb-0000-0000-0000-000000000002', now(),  0.0000, 3.2000, -3.2000, NULL);

INSERT INTO energy_order (order_id, user_id, meter_id, order_type, energy_quantity_kwh, remaining_quantity_kwh, price_per_kwh, status, created_at)
VALUES
  ('cccccccc-0000-0000-0000-000000000001', 'aaaaaaaa-0000-0000-0000-000000000001', 'bbbbbbbb-0000-0000-0000-000000000001',
   'SELL', 10.0000, 10.0000, 6.0000, 'OPEN', now() - interval '1 hour'),
  ('cccccccc-0000-0000-0000-000000000002', 'aaaaaaaa-0000-0000-0000-000000000002', 'bbbbbbbb-0000-0000-0000-000000000002',
   'BUY',   4.0000,  4.0000, 8.0000, 'OPEN', now())
ON CONFLICT DO NOTHING;
