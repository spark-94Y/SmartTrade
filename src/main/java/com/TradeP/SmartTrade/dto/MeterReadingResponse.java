package com.TradeP.SmartTrade.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.TradeP.SmartTrade.entity.MeterReading;

public record MeterReadingResponse(
        UUID readingId,
        UUID meterId,
        LocalDateTime timestamp,
        BigDecimal currentGenerationKwh,
        BigDecimal currentConsumptionKwh,
        BigDecimal netGridFlowKwh,
        BigDecimal batterySocPercentage) {

    public static MeterReadingResponse from(MeterReading r) {
        return new MeterReadingResponse(
                r.getReadingId(),
                r.getMeter().getMeterId(),
                r.getTimestamp(),
                r.getCurrentGenerationKwh(),
                r.getCurrentConsumptionKwh(),
                r.getNetGridFlowKwh(),
                r.getBatterySocPercentage());
    }
}
