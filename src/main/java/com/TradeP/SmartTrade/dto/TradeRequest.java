package com.TradeP.SmartTrade.dto;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Body of POST /api/trades.
 *
 * clearedPricePerKwh is optional. When it is left out, the price of the older
 * (resting) order is used.
 */
public record TradeRequest(
        UUID buyOrderId,
        UUID sellOrderId,
        BigDecimal clearedQuantityKwh,
        BigDecimal clearedPricePerKwh) {
}
