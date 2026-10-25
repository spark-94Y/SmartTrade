package com.TradeP.SmartTrade.config;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

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

/**
 * Seeds the database with sample data on application startup.
 * Uses fixed UUIDs so the data is idempotent — safe to run on every restart.
 * If the seed data already exists (checked via the first user's UUID), it skips.
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

    public DataSeeder(UserRepository userRepo,
                      GridZoneRepository zoneRepo,
                      HouseRepository houseRepo,
                      SmartMeterRepository meterRepo,
                      MeterReadingRepository readingRepo,
                      EnergyOrderRepository orderRepo,
                      EnergyTradeRepository tradeRepo,
                      WalletTransactionRepository walletRepo) {
        this.userRepo    = userRepo;
        this.zoneRepo    = zoneRepo;
        this.houseRepo   = houseRepo;
        this.meterRepo   = meterRepo;
        this.readingRepo = readingRepo;
        this.orderRepo   = orderRepo;
        this.tradeRepo   = tradeRepo;
        this.walletRepo  = walletRepo;
    }

    @Override
    public void run(String... args) {
        // ── Idempotency check: skip if seed data already present ────────────
        if (userRepo.existsById(ALICE_ID)) {
            logger.info("✔ Sample data already exists — skipping seed.");
            return;
        }

        logger.info("⏳ Seeding sample data …");
        LocalDateTime now = LocalDateTime.now();

        // ── 1. Users ────────────────────────────────────────────────────────
        User alice = new User();
        alice.setUserId(ALICE_ID);
        alice.setEmail("alice@smarttrade.io");
        alice.setFullName("Alice Johnson");
        alice.setPasswordHash("$2a$10$dummyHashForAlice000000000000000000000000000000");
        alice.setWalletBalance(new BigDecimal("5000.0000"));
        alice.setRole("PROSUMER");
        alice.setCreatedAt(now);
        userRepo.save(alice);

        User bob = new User();
        bob.setUserId(BOB_ID);
        bob.setEmail("bob@smarttrade.io");
        bob.setFullName("Bob Williams");
        bob.setPasswordHash("$2a$10$dummyHashForBob00000000000000000000000000000000");
        bob.setWalletBalance(new BigDecimal("3200.0000"));
        bob.setRole("PROSUMER");
        bob.setCreatedAt(now);
        userRepo.save(bob);

        // ── 2. Grid Zone ────────────────────────────────────────────────────
        GridZone zone = new GridZone();
        zone.setZoneId(ZONE_ID);
        zone.setZoneName("Zone-Mumbai-West");
        zone.setCurrentSolarIrradiance(new BigDecimal("875.50"));
        zone.setWeatherCondition("CLEAR");
        zone.setTotalAggregateReserveKwh(new BigDecimal("1250.0000"));
        zone.setGridStatus("STABLE");
        zone.setBaseMarketClearingPrice(new BigDecimal("6.5000"));
        zoneRepo.save(zone);

        // ── 3. Houses ───────────────────────────────────────────────────────
        House houseA = new House();
        houseA.setHouseId(HOUSE_A_ID);
        houseA.setUser(alice);
        houseA.setGridZone(zone);
        houseA.setAddress("42, Green Acres, Andheri West, Mumbai 400053");
        houseA.setSolarCapacityKw(new BigDecimal("5.50"));
        houseA.setConnectionType("SOLAR_HYBRID");
        houseA.setCreatedAt(now);
        houseRepo.save(houseA);

        House houseB = new House();
        houseB.setHouseId(HOUSE_B_ID);
        houseB.setUser(bob);
        houseB.setGridZone(zone);
        houseB.setAddress("18, Palm Residency, Bandra East, Mumbai 400051");
        houseB.setSolarCapacityKw(new BigDecimal("3.20"));
        houseB.setConnectionType("SOLAR_ONLY");
        houseB.setCreatedAt(now);
        houseRepo.save(houseB);

        // ── 4. Smart Meters ─────────────────────────────────────────────────
        SmartMeter meterA = new SmartMeter();
        meterA.setMeterId(METER_A_ID);
        meterA.setHouse(houseA);
        meterA.setMeterNumber("SM-MH-2024-00001");
        meterA.setHardwareVersion("HW-v3.1");
        meterA.setFirmwareVersion("FW-v2.4.1");
        meterA.setInstallationDate(LocalDate.of(2024, 6, 15));
        meterA.setStatus("ONLINE");
        meterRepo.save(meterA);

        SmartMeter meterB = new SmartMeter();
        meterB.setMeterId(METER_B_ID);
        meterB.setHouse(houseB);
        meterB.setMeterNumber("SM-MH-2024-00002");
        meterB.setHardwareVersion("HW-v3.1");
        meterB.setFirmwareVersion("FW-v2.4.1");
        meterB.setInstallationDate(LocalDate.of(2024, 7, 20));
        meterB.setStatus("ONLINE");
        meterRepo.save(meterB);

        // ── 5. Meter Readings ───────────────────────────────────────────────
        MeterReading readingA = new MeterReading();
        readingA.setReadingId(READING_A);
        readingA.setMeter(meterA);
        readingA.setReadingValue(new BigDecimal("12.3500"));
        readingA.setReadingType("EXPORT");
        readingA.setTimestamp(now);
        readingRepo.save(readingA);

        MeterReading readingB = new MeterReading();
        readingB.setReadingId(READING_B);
        readingB.setMeter(meterB);
        readingB.setReadingValue(new BigDecimal("8.7200"));
        readingB.setReadingType("IMPORT");
        readingB.setTimestamp(now);
        readingRepo.save(readingB);

        // ── 6. Energy Orders ────────────────────────────────────────────────
        EnergyOrder buyOrder = new EnergyOrder();
        buyOrder.setOrderId(BUY_ORDER);
        buyOrder.setUser(alice);
        buyOrder.setMeter(meterA);
        buyOrder.setOrderType("BUY");
        buyOrder.setEnergyQuantityKwh(new BigDecimal("10.0000"));
        buyOrder.setRemainingQuantityKwh(new BigDecimal("0.0000"));
        buyOrder.setPricePerKwh(new BigDecimal("7.0000"));
        buyOrder.setStatus("FILLED");
        buyOrder.setCreatedAt(now);
        buyOrder.setExpiresAt(now.plusHours(24));
        orderRepo.save(buyOrder);

        EnergyOrder sellOrder = new EnergyOrder();
        sellOrder.setOrderId(SELL_ORDER);
        sellOrder.setUser(bob);
        sellOrder.setMeter(meterB);
        sellOrder.setOrderType("SELL");
        sellOrder.setEnergyQuantityKwh(new BigDecimal("10.0000"));
        sellOrder.setRemainingQuantityKwh(new BigDecimal("0.0000"));
        sellOrder.setPricePerKwh(new BigDecimal("6.5000"));
        sellOrder.setStatus("FILLED");
        sellOrder.setCreatedAt(now);
        sellOrder.setExpiresAt(now.plusHours(24));
        orderRepo.save(sellOrder);

        // ── 7. Energy Trade ─────────────────────────────────────────────────
        EnergyTrade trade = new EnergyTrade();
        trade.setTradeId(TRADE_ID);
        trade.setBuyOrder(buyOrder);
        trade.setSellOrder(sellOrder);
        trade.setBuyer(alice);
        trade.setSeller(bob);
        trade.setClearedQuantityKwh(new BigDecimal("10.0000"));
        trade.setClearedPricePerKwh(new BigDecimal("6.7500"));
        trade.setTotalCost(new BigDecimal("67.5000"));
        trade.setExecutedAt(now);
        trade.setStatus("COMPLETED");
        tradeRepo.save(trade);

        // ── 8. Wallet Transactions ──────────────────────────────────────────
        WalletTransaction debit = new WalletTransaction();
        debit.setTransactionId(WALLET_A);
        debit.setUser(alice);
        debit.setTrade(trade);
        debit.setAmount(new BigDecimal("-67.5000"));
        debit.setTransactionType("DEBIT");
        debit.setDescription("Payment for 10 kWh purchase from Bob");
        debit.setTimestamp(now);
        walletRepo.save(debit);

        WalletTransaction credit = new WalletTransaction();
        credit.setTransactionId(WALLET_B);
        credit.setUser(bob);
        credit.setTrade(trade);
        credit.setAmount(new BigDecimal("67.5000"));
        credit.setTransactionType("CREDIT");
        credit.setDescription("Revenue for 10 kWh sale to Alice");
        credit.setTimestamp(now);
        walletRepo.save(credit);

        logger.info("✅ Sample data seeded successfully! (2 users, 1 zone, 2 houses, 2 meters, 2 readings, 2 orders, 1 trade, 2 wallet txns)");
    }
}
