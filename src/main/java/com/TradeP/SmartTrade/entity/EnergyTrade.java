package com.TradeP.SmartTrade.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "energy_trade")
public class EnergyTrade {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "trade_id", updatable = false, nullable = false)
    private UUID tradeId;

    @ManyToOne
    @JoinColumn(name = "buy_order_id", nullable = false)
    private EnergyOrder buyOrder;

    @ManyToOne
    @JoinColumn(name = "sell_order_id", nullable = false)
    private EnergyOrder sellOrder;

    @ManyToOne
    @JoinColumn(name = "buyer_id", nullable = false)
    private User buyer;

    @ManyToOne
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    @Column(name = "cleared_quantity_kwh", precision = 10, scale = 4, nullable = false)
    private BigDecimal clearedQuantityKwh;

    @Column(name = "cleared_price_per_kwh", precision = 10, scale = 4, nullable = false)
    private BigDecimal clearedPricePerKwh;

    @Column(name = "total_cost", precision = 12, scale = 4, nullable = false)
    private BigDecimal totalCost;

    @CreationTimestamp
    @Column(name = "executed_at", updatable = false, nullable = false)
    private LocalDateTime executedAt;

    @Column(name = "status", length = 30, nullable = false)
    private String status = "COMPLETED";

    @JsonIgnore
    @OneToMany(mappedBy = "trade")
    private List<WalletTransaction> walletTransactions;

    public UUID getTradeId() {
        return tradeId;
    }

    public void setTradeId(UUID tradeId) {
        this.tradeId = tradeId;
    }

    public EnergyOrder getBuyOrder() {
        return buyOrder;
    }

    public void setBuyOrder(EnergyOrder buyOrder) {
        this.buyOrder = buyOrder;
    }

    public UUID getBuyOrderId() {
        return buyOrder != null ? buyOrder.getOrderId() : null;
    }

    public void setBuyOrderId(UUID buyOrderId) {
        if (buyOrderId != null) {
            EnergyOrder order = new EnergyOrder();
            order.setOrderId(buyOrderId);
            this.buyOrder = order;
        } else {
            this.buyOrder = null;
        }
    }

    public EnergyOrder getSellOrder() {
        return sellOrder;
    }

    public void setSellOrder(EnergyOrder sellOrder) {
        this.sellOrder = sellOrder;
    }

    public UUID getSellOrderId() {
        return sellOrder != null ? sellOrder.getOrderId() : null;
    }

    public void setSellOrderId(UUID sellOrderId) {
        if (sellOrderId != null) {
            EnergyOrder order = new EnergyOrder();
            order.setOrderId(sellOrderId);
            this.sellOrder = order;
        } else {
            this.sellOrder = null;
        }
    }

    public User getBuyer() {
        return buyer;
    }

    public void setBuyer(User buyer) {
        this.buyer = buyer;
    }

    public UUID getBuyerId() {
        return buyer != null ? buyer.getUserId() : null;
    }

    public void setBuyerId(UUID buyerId) {
        if (buyerId != null) {
            User user = new User();
            user.setUserId(buyerId);
            this.buyer = user;
        } else {
            this.buyer = null;
        }
    }

    public User getSeller() {
        return seller;
    }

    public void setSeller(User seller) {
        this.seller = seller;
    }

    public UUID getSellerId() {
        return seller != null ? seller.getUserId() : null;
    }

    public void setSellerId(UUID sellerId) {
        if (sellerId != null) {
            User user = new User();
            user.setUserId(sellerId);
            this.seller = user;
        } else {
            this.seller = null;
        }
    }

    public BigDecimal getClearedQuantityKwh() {
        return clearedQuantityKwh;
    }

    public void setClearedQuantityKwh(BigDecimal clearedQuantityKwh) {
        this.clearedQuantityKwh = clearedQuantityKwh;
    }

    public BigDecimal getClearedPricePerKwh() {
        return clearedPricePerKwh;
    }

    public void setClearedPricePerKwh(BigDecimal clearedPricePerKwh) {
        this.clearedPricePerKwh = clearedPricePerKwh;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }

    public LocalDateTime getExecutedAt() {
        return executedAt;
    }

    public void setExecutedAt(LocalDateTime executedAt) {
        this.executedAt = executedAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<WalletTransaction> getWalletTransactions() {
        return walletTransactions;
    }

    public void setWalletTransactions(List<WalletTransaction> walletTransactions) {
        this.walletTransactions = walletTransactions;
    }
}