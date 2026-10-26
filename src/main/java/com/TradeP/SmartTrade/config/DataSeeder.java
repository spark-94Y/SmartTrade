package com.TradeP.SmartTrade.config;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import com.TradeP.SmartTrade.entity.EnergyOrder;
import com.TradeP.SmartTrade.entity.EnergyTrade;
import com.TradeP.SmartTrade.entity.GridZone;
import com.TradeP.SmartTrade.entity.House;
import com.TradeP.SmartTrade.entity.MeterReading;
import com.TradeP.SmartTrade.entity.SmartMeter;
import com.TradeP.SmartTrade.entity.User;
import com.TradeP.SmartTrade.entity.WalletTransaction;
import com.TradeP.SmartTrade.repository.EnergyOrderRepository;
import com.TradeP.SmartTrade.repository.EnergyTradeRepository;
import com.TradeP.SmartTrade.repository.GridZoneRepository;
import com.TradeP.SmartTrade.repository.HouseRepository;
import com.TradeP.SmartTrade.repository.MeterReadingRepository;
import com.TradeP.SmartTrade.repository.SmartMeterRepository;
import com.TradeP.SmartTrade.repository.UserRepository;
import com.TradeP.SmartTrade.repository.WalletTransactionRepository;

import jakarta.persistence.EntityManager;

/**
 * Seeds the database with sample data on application startup.
 * Uses fixed UUIDs so the data is idempotent — safe to run on every restart.
 * If ANY data already exists in any table, the seeder skips entirely.
 * Uses EntityManager.merge() inside a programmatic transaction to handle
 * entities with pre-assigned UUIDs and @GeneratedValue annotation.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataSeeder.class);

    // ── Fixed UUIDs (matching the SQL init script) ──────────────────────────
    private static final UUID ALICE_ID   = UUID.fromString("a1111111-1111-1111-1111-111111111111");
    private static final UUID BOB_ID     = UUID.fromString("b2222222-2222-2222-2222-222222222222");
    private static final UUID ZONE_ID    = UUID.fromString("c3333333-3333-3333-3333-333333333333");
    private static final UUID HOUSE_A_ID = UUID.fromString("d4444444-4444-4444-4444-444444444444");
    private static final UUID HOUSE_B_ID = UUID.fromString("d5555555-5555-5555-5555-555555555555");
    private static final UUID METER_A_ID = UUID.fromString("e6666666-6666-6666-6666-666666666666");
    private static final UUID METER_B_ID = UUID.fromString("e7777777-7777-7777-7777-777777777777");
    private static final UUID READING_A  = UUID.fromString("f8888888-8888-8888-8888-888888888888");
    private static final UUID READING_B  = UUID.fromString("f9999999-9999-9999-9999-999999999999");
    private static final UUID BUY_ORDER  = UUID.fromString("aa000000-0000-0000-0000-000000000001");
    private static final UUID SELL_ORDER = UUID.fromString("aa000000-0000-0000-0000-000000000002");
    private static final UUID TRADE_ID   = UUID.fromString("bb000000-0000-0000-0000-000000000001");
    private static final UUID WALLET_A   = UUID.fromString("cc000000-0000-0000-0000-000000000001");
    private static final UUID WALLET_B   = UUID.fromString("cc000000-0000-0000-0000-000000000002");

    private final UserRepository userRepo;
    private final GridZoneRepository zoneRepo;
    private final HouseRepository houseRepo;
    private final SmartMeterRepository meterRepo;
    private final MeterReadingRepository readingRepo;
    private final EnergyOrderRepository orderRepo;
    private final EnergyTradeRepository tradeRepo;
    private final WalletTransactionRepository walletRepo;
    private final EntityManager entityManager;
    private final TransactionTemplate transactionTemplate;

    public DataSeeder(UserRepository userRepo,
                      GridZoneRepository zoneRepo,
                      HouseRepository houseRepo,
                      SmartMeterRepository meterRepo,
                      MeterReadingRepository readingRepo,
                      EnergyOrderRepository orderRepo,
                      EnergyTradeRepository tradeRepo,
                      WalletTransactionRepository walletRepo,
                      EntityManager entityManager,
                      TransactionTemplate transactionTemplate) {
        this.userRepo    = userRepo;
        this.zoneRepo    = zoneRepo;
        this.houseRepo   = houseRepo;
        this.meterRepo   = meterRepo;
        this.readingRepo = readingRepo;
        this.orderRepo   = orderRepo;
        this.tradeRepo   = tradeRepo;
        this.walletRepo  = walletRepo;
        this.entityManager = entityManager;
        this.transactionTemplate = transactionTemplate;
    }

    @Override
    public void run(String... args) {
        try {
            // ── Idempotency check: skip if ANY data already present ─────────
            if (userRepo.count() > 0 || zoneRepo.count() > 0 || houseRepo.count() > 0
                    || meterRepo.count() > 0 || readingRepo.count() > 0 || orderRepo.count() > 0
                    || tradeRepo.count() > 0 || walletRepo.count() > 0) {
                logger.info("✔ Database already contains data — skipping seed.");
                return;
            }

            logger.info("⏳ Seeding sample data …");

            transactionTemplate.executeWithoutResult(status -> seedData());

            logger.info("✅ Sample data seeded successfully! (2 users, 1 zone, 2 houses, 2 meters, 2 readings, 2 orders, 1 trade, 2 wallet txns)");

        } catch (Exception e) {
            // Log the error but NEVER crash the application because of seeding
            logger.error("⚠ Data seeding failed — application will continue without sample data. Error: {}", e.getMessage());
        }
    }

    /**
     * Uses native SQL INSERT statements to seed data, bypassing Hibernate's
     * persist/merge issues with manually-assigned UUIDs on @GeneratedValue entities.
     */
    private void seedData() {
        LocalDateTime now = LocalDateTime.now();

        // ── 1. Users ────────────────────────────────────────────────────────
        entityManager.createNativeQuery(
            "INSERT INTO \"user\" (user_id, email, full_name, password_hash, wallet_balance, role, created_at) " +
            "VALUES (:id, :email, :name, :hash, :balance, :role, :created)")
            .setParameter("id", ALICE_ID)
            .setParameter("email", "alice@smarttrade.io")
            .setParameter("name", "Alice Johnson")
            .setParameter("hash", "$2a$10$dummyHashForAlice000000000000000000000000000000")
            .setParameter("balance", new BigDecimal("5000.0000"))
            .setParameter("role", "PROSUMER")
            .setParameter("created", now)
            .executeUpdate();

        entityManager.createNativeQuery(
            "INSERT INTO \"user\" (user_id, email, full_name, password_hash, wallet_balance, role, created_at) " +
            "VALUES (:id, :email, :name, :hash, :balance, :role, :created)")
            .setParameter("id", BOB_ID)
            .setParameter("email", "bob@smarttrade.io")
            .setParameter("name", "Bob Williams")
            .setParameter("hash", "$2a$10$dummyHashForBob00000000000000000000000000000000")
            .setParameter("balance", new BigDecimal("3200.0000"))
            .setParameter("role", "PROSUMER")
            .setParameter("created", now)
            .executeUpdate();

        // ── 2. Grid Zone ────────────────────────────────────────────────────
        entityManager.createNativeQuery(
            "INSERT INTO grid_zone (zone_id, zone_name, current_solar_irradiance, weather_condition, " +
            "total_aggregate_reserve_kwh, grid_status, base_market_clearing_price) " +
            "VALUES (:id, :name, :irradiance, :weather, :reserve, :status, :price)")
            .setParameter("id", ZONE_ID)
            .setParameter("name", "Zone-Mumbai-West")
            .setParameter("irradiance", new BigDecimal("875.50"))
            .setParameter("weather", "CLEAR")
            .setParameter("reserve", new BigDecimal("1250.0000"))
            .setParameter("status", "STABLE")
            .setParameter("price", new BigDecimal("6.5000"))
            .executeUpdate();

        // ── 3. Houses ───────────────────────────────────────────────────────
        entityManager.createNativeQuery(
            "INSERT INTO house (house_id, user_id, zone_id, address, solar_capacity_kw, connection_type, created_at) " +
            "VALUES (:id, :userId, :zoneId, :address, :capacity, :type, :created)")
            .setParameter("id", HOUSE_A_ID)
            .setParameter("userId", ALICE_ID)
            .setParameter("zoneId", ZONE_ID)
            .setParameter("address", "42, Green Acres, Andheri West, Mumbai 400053")
            .setParameter("capacity", new BigDecimal("5.50"))
            .setParameter("type", "SOLAR_HYBRID")
            .setParameter("created", now)
            .executeUpdate();

        entityManager.createNativeQuery(
            "INSERT INTO house (house_id, user_id, zone_id, address, solar_capacity_kw, connection_type, created_at) " +
            "VALUES (:id, :userId, :zoneId, :address, :capacity, :type, :created)")
            .setParameter("id", HOUSE_B_ID)
            .setParameter("userId", BOB_ID)
            .setParameter("zoneId", ZONE_ID)
            .setParameter("address", "18, Palm Residency, Bandra East, Mumbai 400051")
            .setParameter("capacity", new BigDecimal("3.20"))
            .setParameter("type", "SOLAR_ONLY")
            .setParameter("created", now)
            .executeUpdate();

        // ── 4. Smart Meters ─────────────────────────────────────────────────
        entityManager.createNativeQuery(
            "INSERT INTO smart_meter (meter_id, house_id, meter_number, hardware_version, firmware_version, installation_date, status) " +
            "VALUES (:id, :houseId, :number, :hw, :fw, :installDate, :status)")
            .setParameter("id", METER_A_ID)
            .setParameter("houseId", HOUSE_A_ID)
            .setParameter("number", "SM-MH-2024-00001")
            .setParameter("hw", "HW-v3.1")
            .setParameter("fw", "FW-v2.4.1")
            .setParameter("installDate", LocalDate.of(2024, 6, 15))
            .setParameter("status", "ONLINE")
            .executeUpdate();

        entityManager.createNativeQuery(
            "INSERT INTO smart_meter (meter_id, house_id, meter_number, hardware_version, firmware_version, installation_date, status) " +
            "VALUES (:id, :houseId, :number, :hw, :fw, :installDate, :status)")
            .setParameter("id", METER_B_ID)
            .setParameter("houseId", HOUSE_B_ID)
            .setParameter("number", "SM-MH-2024-00002")
            .setParameter("hw", "HW-v3.1")
            .setParameter("fw", "FW-v2.4.1")
            .setParameter("installDate", LocalDate.of(2024, 7, 20))
            .setParameter("status", "ONLINE")
            .executeUpdate();

        // ── 5. Meter Readings ───────────────────────────────────────────────
        entityManager.createNativeQuery(
            "INSERT INTO meter_reading (reading_id, meter_id, reading_value, reading_type, \"timestamp\") " +
            "VALUES (:id, :meterId, :value, :type, :ts)")
            .setParameter("id", READING_A)
            .setParameter("meterId", METER_A_ID)
            .setParameter("value", new BigDecimal("12.3500"))
            .setParameter("type", "EXPORT")
            .setParameter("ts", now)
            .executeUpdate();

        entityManager.createNativeQuery(
            "INSERT INTO meter_reading (reading_id, meter_id, reading_value, reading_type, \"timestamp\") " +
            "VALUES (:id, :meterId, :value, :type, :ts)")
            .setParameter("id", READING_B)
            .setParameter("meterId", METER_B_ID)
            .setParameter("value", new BigDecimal("8.7200"))
            .setParameter("type", "IMPORT")
            .setParameter("ts", now)
            .executeUpdate();

        // ── 6. Energy Orders ────────────────────────────────────────────────
        entityManager.createNativeQuery(
            "INSERT INTO energy_order (order_id, user_id, meter_id, order_type, energy_quantity_kwh, " +
            "remaining_quantity_kwh, price_per_kwh, status, created_at, expires_at) " +
            "VALUES (:id, :userId, :meterId, :type, :qty, :remaining, :price, :status, :created, :expires)")
            .setParameter("id", BUY_ORDER)
            .setParameter("userId", ALICE_ID)
            .setParameter("meterId", METER_A_ID)
            .setParameter("type", "BUY")
            .setParameter("qty", new BigDecimal("10.0000"))
            .setParameter("remaining", new BigDecimal("0.0000"))
            .setParameter("price", new BigDecimal("7.0000"))
            .setParameter("status", "FILLED")
            .setParameter("created", now)
            .setParameter("expires", now.plusHours(24))
            .executeUpdate();

        entityManager.createNativeQuery(
            "INSERT INTO energy_order (order_id, user_id, meter_id, order_type, energy_quantity_kwh, " +
            "remaining_quantity_kwh, price_per_kwh, status, created_at, expires_at) " +
            "VALUES (:id, :userId, :meterId, :type, :qty, :remaining, :price, :status, :created, :expires)")
            .setParameter("id", SELL_ORDER)
            .setParameter("userId", BOB_ID)
            .setParameter("meterId", METER_B_ID)
            .setParameter("type", "SELL")
            .setParameter("qty", new BigDecimal("10.0000"))
            .setParameter("remaining", new BigDecimal("0.0000"))
            .setParameter("price", new BigDecimal("6.5000"))
            .setParameter("status", "FILLED")
            .setParameter("created", now)
            .setParameter("expires", now.plusHours(24))
            .executeUpdate();

        // ── 7. Energy Trade ─────────────────────────────────────────────────
        entityManager.createNativeQuery(
            "INSERT INTO energy_trade (trade_id, buy_order_id, sell_order_id, buyer_id, seller_id, " +
            "cleared_quantity_kwh, cleared_price_per_kwh, total_cost, executed_at, status) " +
            "VALUES (:id, :buyOrderId, :sellOrderId, :buyerId, :sellerId, :qty, :price, :cost, :executed, :status)")
            .setParameter("id", TRADE_ID)
            .setParameter("buyOrderId", BUY_ORDER)
            .setParameter("sellOrderId", SELL_ORDER)
            .setParameter("buyerId", ALICE_ID)
            .setParameter("sellerId", BOB_ID)
            .setParameter("qty", new BigDecimal("10.0000"))
            .setParameter("price", new BigDecimal("6.7500"))
            .setParameter("cost", new BigDecimal("67.5000"))
            .setParameter("executed", now)
            .setParameter("status", "COMPLETED")
            .executeUpdate();

        // ── 8. Wallet Transactions ──────────────────────────────────────────
        entityManager.createNativeQuery(
            "INSERT INTO wallet_transaction (transaction_id, user_id, trade_id, amount, transaction_type, description, \"timestamp\") " +
            "VALUES (:id, :userId, :tradeId, :amount, :type, :desc, :ts)")
            .setParameter("id", WALLET_A)
            .setParameter("userId", ALICE_ID)
            .setParameter("tradeId", TRADE_ID)
            .setParameter("amount", new BigDecimal("-67.5000"))
            .setParameter("type", "DEBIT")
            .setParameter("desc", "Payment for 10 kWh purchase from Bob")
            .setParameter("ts", now)
            .executeUpdate();

        entityManager.createNativeQuery(
            "INSERT INTO wallet_transaction (transaction_id, user_id, trade_id, amount, transaction_type, description, \"timestamp\") " +
            "VALUES (:id, :userId, :tradeId, :amount, :type, :desc, :ts)")
            .setParameter("id", WALLET_B)
            .setParameter("userId", BOB_ID)
            .setParameter("tradeId", TRADE_ID)
            .setParameter("amount", new BigDecimal("67.5000"))
            .setParameter("type", "CREDIT")
            .setParameter("desc", "Revenue for 10 kWh sale to Alice")
            .setParameter("ts", now)
            .executeUpdate();

        entityManager.flush();
    }
}
