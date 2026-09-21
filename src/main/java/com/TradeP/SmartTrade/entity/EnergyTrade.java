package com.TradeP.SmartTrade.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * EnergyTrade Entity
 * ------------------
 * trade_id (PK), buy_order_id (FK), sell_order_id (FK), cleared_quantity_kwh,
 * cleared_price_per_kwh, total_cost, executed_at
 *
 * One row = one match between a BUY order and a SELL order. A trade can clear
 * only part of an order, so one order can appear in many trades.
 * Trades are an immutable ledger: they are created, never updated or deleted.
 */
@Entity
@Table(name = "energy_trade")
public class EnergyTrade {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "trade_id", nullable = false, updatable = false)
    private UUID tradeId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "buy_order_id", nullable = false, updatable = false)
    private EnergyOrder buyOrder;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sell_order_id", nullable = false, updatable = false)
    private EnergyOrder sellOrder;

    @Column(name = "cleared_quantity_kwh", nullable = false, updatable = false, precision = 10, scale = 4)
    private BigDecimal clearedQuantityKwh;

    @Column(name = "cleared_price_per_kwh", nullable = false, updatable = false, precision = 10, scale = 4)
    private BigDecimal clearedPricePerKwh;

    /** cleared_quantity_kwh x cleared_price_per_kwh, rounded to 4 decimals. */
    @Column(name = "total_cost", nullable = false, updatable = false, precision = 14, scale = 4)
    private BigDecimal totalCost;

    @CreationTimestamp
    @Column(name = "executed_at", nullable = false, updatable = false)
    private LocalDateTime executedAt;

    public EnergyTrade() {
    }

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

    public EnergyOrder getSellOrder() {
        return sellOrder;
    }

    public void setSellOrder(EnergyOrder sellOrder) {
        this.sellOrder = sellOrder;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EnergyTrade that = (EnergyTrade) o;
        return tradeId != null && Objects.equals(tradeId, that.tradeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tradeId);
    }
}
