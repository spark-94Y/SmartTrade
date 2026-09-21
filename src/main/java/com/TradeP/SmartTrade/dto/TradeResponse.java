package com.TradeP.SmartTrade.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.TradeP.SmartTrade.entity.EnergyTrade;

public record TradeResponse(
        UUID tradeId,
        UUID buyOrderId,
        UUID sellOrderId,
        UUID buyerUserId,
        UUID sellerUserId,
        BigDecimal clearedQuantityKwh,
        BigDecimal clearedPricePerKwh,
        BigDecimal totalCost,
        LocalDateTime executedAt) {

    public static TradeResponse from(EnergyTrade t) {
        return new TradeResponse(
                t.getTradeId(),
                t.getBuyOrder().getOrderId(),
                t.getSellOrder().getOrderId(),
                t.getBuyOrder().getUser().getUserId(),
                t.getSellOrder().getUser().getUserId(),
                t.getClearedQuantityKwh(),
                t.getClearedPricePerKwh(),
                t.getTotalCost(),
                t.getExecutedAt());
    }
}
