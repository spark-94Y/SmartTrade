package com.TradeP.SmartTrade.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Body of POST /api/readings.
 *
 * Optional: timestamp (defaults to now), netGridFlowKwh (defaults to
 * generation - consumption) and batterySocPercentage (leave out when the meter
 * has no battery).
 */
public record MeterReadingRequest(
        UUID meterId,
        LocalDateTime timestamp,
        BigDecimal currentGenerationKwh,
        BigDecimal currentConsumptionKwh,
        BigDecimal netGridFlowKwh,
        BigDecimal batterySocPercentage) {
}
