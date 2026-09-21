package com.TradeP.SmartTrade.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.TradeP.SmartTrade.dto.TradeRequest;
import com.TradeP.SmartTrade.dto.TradeResponse;
import com.TradeP.SmartTrade.entity.EnergyOrder;
import com.TradeP.SmartTrade.entity.EnergyTrade;
import com.TradeP.SmartTrade.entity.User;
import com.TradeP.SmartTrade.entity.WalletTransaction;
import com.TradeP.SmartTrade.repository.EnergyOrderRepository;
import com.TradeP.SmartTrade.repository.EnergyTradeRepository;
import com.TradeP.SmartTrade.repository.UserRepository;
import com.TradeP.SmartTrade.repository.WalletTransactionRepository;

/**
 * Executes and reads energy trades.
 *
 * executeTrade() is one database transaction. Either everything below happens
 * or nothing does:
 *   1. an EnergyTrade row is written
 *   2. both orders have their remaining_quantity_kwh reduced and their status
 *      moved to PARTIALLY_FILLED or FILLED
 *   3. the buyer's wallet is debited and the seller's wallet is credited
 *   4. two WalletTransaction rows (DEBIT / CREDIT) are written for the audit trail
 */
@Service
public class EnergyTradeService {

    public static final String BUY = "BUY";
    public static final String SELL = "SELL";

    public static final String STATUS_OPEN = "OPEN";
    public static final String STATUS_PARTIALLY_FILLED = "PARTIALLY_FILLED";
    public static final String STATUS_FILLED = "FILLED";

    private static final int SCALE = 4;

    private final EnergyTradeRepository tradeRepo;
    private final EnergyOrderRepository orderRepo;
    private final UserRepository userRepo;
    private final WalletTransactionRepository walletTxRepo;

    public EnergyTradeService(EnergyTradeRepository tradeRepo,
                              EnergyOrderRepository orderRepo,
                              UserRepository userRepo,
                              WalletTransactionRepository walletTxRepo) {
        this.tradeRepo = tradeRepo;
        this.orderRepo = orderRepo;
        this.userRepo = userRepo;
        this.walletTxRepo = walletTxRepo;
    }

    // ------------------------------------------------------------------
    // Execute
    // ------------------------------------------------------------------

    @Transactional
    public TradeResponse executeTrade(TradeRequest req) {
        // ---- 1. input checks -------------------------------------------
        if (req == null || req.buyOrderId() == null || req.sellOrderId() == null) {
            throw error(HttpStatus.BAD_REQUEST, "buyOrderId and sellOrderId are required");
        }
        if (req.buyOrderId().equals(req.sellOrderId())) {
            throw error(HttpStatus.BAD_REQUEST, "An order cannot be matched with itself");
        }
        if (req.clearedQuantityKwh() == null) {
            throw error(HttpStatus.BAD_REQUEST, "clearedQuantityKwh is required");
        }
        BigDecimal quantity = req.clearedQuantityKwh().setScale(SCALE, RoundingMode.HALF_UP);
        if (quantity.signum() <= 0) {
            throw error(HttpStatus.BAD_REQUEST, "clearedQuantityKwh must be greater than zero");
        }
        if (req.clearedPricePerKwh() != null
                && req.clearedPricePerKwh().setScale(SCALE, RoundingMode.HALF_UP).signum() <= 0) {
            throw error(HttpStatus.BAD_REQUEST, "clearedPricePerKwh must be greater than zero");
        }

        // ---- 2. lock both orders, lowest id first so concurrent trades cannot deadlock
        UUID firstId = req.buyOrderId().compareTo(req.sellOrderId()) < 0
                ? req.buyOrderId() : req.sellOrderId();
        UUID secondId = firstId.equals(req.buyOrderId()) ? req.sellOrderId() : req.buyOrderId();
        EnergyOrder first = lockOrder(firstId);
        EnergyOrder second = lockOrder(secondId);
        EnergyOrder buy = firstId.equals(req.buyOrderId()) ? first : second;
        EnergyOrder sell = (buy == first) ? second : first;

        // ---- 3. order rules --------------------------------------------
        if (!BUY.equalsIgnoreCase(buy.getOrderType())) {
            throw error(HttpStatus.CONFLICT, "buyOrderId does not point to a BUY order");
        }
        if (!SELL.equalsIgnoreCase(sell.getOrderType())) {
            throw error(HttpStatus.CONFLICT, "sellOrderId does not point to a SELL order");
        }
        requireTradable(buy, "Buy order");
        requireTradable(sell, "Sell order");

        if (quantity.compareTo(buy.getRemainingQuantityKwh()) > 0) {
            throw error(HttpStatus.CONFLICT, "Quantity is more than the buy order still has left");
        }
        if (quantity.compareTo(sell.getRemainingQuantityKwh()) > 0) {
            throw error(HttpStatus.CONFLICT, "Quantity is more than the sell order still has left");
        }

        // ---- 4. price: must sit between the seller's ask and the buyer's limit
        BigDecimal price = (req.clearedPricePerKwh() != null
                ? req.clearedPricePerKwh()
                : restingOrderPrice(buy, sell)).setScale(SCALE, RoundingMode.HALF_UP);
        if (price.compareTo(buy.getPricePerKwh()) > 0) {
            throw error(HttpStatus.CONFLICT, "Cleared price is above the buyer's limit price");
        }
        if (price.compareTo(sell.getPricePerKwh()) < 0) {
            throw error(HttpStatus.CONFLICT, "Cleared price is below the seller's ask price");
        }
        BigDecimal totalCost = quantity.multiply(price).setScale(SCALE, RoundingMode.HALF_UP);

        // ---- 5. wallets: lock lowest id first, check the buyer can pay --
        UUID buyerId = buy.getUser().getUserId();
        UUID sellerId = sell.getUser().getUserId();
        if (buyerId.equals(sellerId)) {
            throw error(HttpStatus.CONFLICT, "Buyer and seller must be different users");
        }
        User buyer;
        User seller;
        if (buyerId.compareTo(sellerId) < 0) {
            buyer = lockUser(buyerId);
            seller = lockUser(sellerId);
        } else {
            seller = lockUser(sellerId);
            buyer = lockUser(buyerId);
        }
        if (buyer.getWalletBalance().compareTo(totalCost) < 0) {
            throw error(HttpStatus.CONFLICT, "Buyer's wallet balance is too low for this trade");
        }

        // ---- 6. write everything --------------------------------------
        EnergyTrade trade = new EnergyTrade();
        trade.setBuyOrder(buy);
        trade.setSellOrder(sell);
        trade.setClearedQuantityKwh(quantity);
        trade.setClearedPricePerKwh(price);
        trade.setTotalCost(totalCost);
        trade = tradeRepo.saveAndFlush(trade); // flush so executed_at is filled in

        // buy, sell, buyer and seller are managed entities: changes are saved on commit
        applyFill(buy, quantity);
        applyFill(sell, quantity);

        buyer.setWalletBalance(buyer.getWalletBalance().subtract(totalCost));
        seller.setWalletBalance(seller.getWalletBalance().add(totalCost));

        String detail = quantity.toPlainString() + " kWh @ " + price.toPlainString() + " per kWh";
        walletTxRepo.save(new WalletTransaction(buyerId, trade.getTradeId(), totalCost, "DEBIT",
                "Energy purchase: " + detail));
        walletTxRepo.save(new WalletTransaction(sellerId, trade.getTradeId(), totalCost, "CREDIT",
                "Energy sale: " + detail));

        return TradeResponse.from(trade);
    }

    // ------------------------------------------------------------------
    // Read
    // ------------------------------------------------------------------

    @Transactional(readOnly = true)
    public TradeResponse getTrade(UUID tradeId) {
        EnergyTrade trade = tradeRepo.findById(tradeId)
                .orElseThrow(() -> error(HttpStatus.NOT_FOUND, "Trade not found"));
        return TradeResponse.from(trade);
    }

    @Transactional(readOnly = true)
    public List<TradeResponse> listAll() {
        return tradeRepo.findAllByOrderByExecutedAtDesc().stream().map(TradeResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<TradeResponse> listForUser(UUID userId) {
        if (!userRepo.existsById(userId)) {
            throw error(HttpStatus.NOT_FOUND, "User not found");
        }
        return tradeRepo.findAllByParticipant(userId).stream().map(TradeResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<TradeResponse> listForOrder(UUID orderId) {
        if (!orderRepo.existsById(orderId)) {
            throw error(HttpStatus.NOT_FOUND, "Order not found");
        }
        return tradeRepo.findAllByOrderId(orderId).stream().map(TradeResponse::from).toList();
    }

    // ------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------

    private EnergyOrder lockOrder(UUID orderId) {
        return orderRepo.findByIdForUpdate(orderId)
                .orElseThrow(() -> error(HttpStatus.NOT_FOUND, "Order not found: " + orderId));
    }

    private User lockUser(UUID userId) {
        return userRepo.findByIdForUpdate(userId)
                .orElseThrow(() -> error(HttpStatus.NOT_FOUND, "User not found: " + userId));
    }

    private void requireTradable(EnergyOrder order, String label) {
        String status = order.getStatus();
        if (!STATUS_OPEN.equalsIgnoreCase(status) && !STATUS_PARTIALLY_FILLED.equalsIgnoreCase(status)) {
            throw error(HttpStatus.CONFLICT, label + " is not open for trading (status: " + status + ")");
        }
        if (order.getExpiresAt() != null && !order.getExpiresAt().isAfter(LocalDateTime.now())) {
            throw error(HttpStatus.CONFLICT, label + " has expired");
        }
    }

    /** Price-time rule: when no price is given, the order that was placed first sets the price. */
    private BigDecimal restingOrderPrice(EnergyOrder buy, EnergyOrder sell) {
        if (buy.getCreatedAt() != null && sell.getCreatedAt() != null
                && buy.getCreatedAt().isBefore(sell.getCreatedAt())) {
            return buy.getPricePerKwh();
        }
        return sell.getPricePerKwh();
    }

    private void applyFill(EnergyOrder order, BigDecimal quantity) {
        BigDecimal remaining = order.getRemainingQuantityKwh().subtract(quantity);
        order.setRemainingQuantityKwh(remaining);
        order.setStatus(remaining.signum() == 0 ? STATUS_FILLED : STATUS_PARTIALLY_FILLED);
    }

    private static ResponseStatusException error(HttpStatus status, String message) {
        return new ResponseStatusException(status, message);
    }
}
