package com.TradeP.SmartTrade.dto;

import java.math.BigDecimal;
import java.util.UUID;

/** Body of POST /api/meters and PUT /api/meters/{id}. userId and zoneId are required. */
public record SmartMeterRequest(
        UUID userId,
        UUID zoneId,
        String hardwareVersion,
        String firmwareVersion,
        BigDecimal latitude,
        BigDecimal longitude,
        String status) {
}
