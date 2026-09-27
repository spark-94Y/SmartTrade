package com.TradeP.SmartTrade.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "energy_trade")
public class EnergyTrade {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "trade_id", updatable = false, nullable = false)
    private UUID tradeId;

    @Column(name = "buy_order_id", nullable = false)
    private UUID buyOrderId;

    @Column(name = "sell_order_id", nullable = false)
    private UUID sellOrderId;

    @Column(name = "buyer_id", nullable = false)
    private UUID buyerId;

    @Column(name = "seller_id", nullable = false)
    private UUID sellerId;

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

    public UUID getTradeId() {
        return tradeId;
    }

    public void setTradeId(UUID tradeId) {
        this.tradeId = tradeId;
    }

    public UUID getBuyOrderId() {
        return buyOrderId;
    }

    public void setBuyOrderId(UUID buyOrderId) {
        this.buyOrderId = buyOrderId;
    }

    public UUID getSellOrderId() {
        return sellOrderId;
    }

    public void setSellOrderId(UUID sellOrderId) {
        this.sellOrderId = sellOrderId;
    }

    public UUID getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(UUID buyerId) {
        this.buyerId = buyerId;
    }

    public UUID getSellerId() {
        return sellerId;
    }

    public void setSellerId(UUID sellerId) {
        this.sellerId = sellerId;
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
}