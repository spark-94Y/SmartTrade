package com.TradeP.SmartTrade.dto;

import java.math.BigDecimal;
import java.util.UUID;

import com.TradeP.SmartTrade.entity.SmartMeter;

public record SmartMeterResponse(
        UUID meterId,
        UUID userId,
        UUID zoneId,
        String hardwareVersion,
        String firmwareVersion,
        BigDecimal latitude,
        BigDecimal longitude,
        String status) {

    public static SmartMeterResponse from(SmartMeter m) {
        return new SmartMeterResponse(
                m.getMeterId(),
                m.getUser().getUserId(),
                m.getZone().getZoneId(),
                m.getHardwareVersion(),
                m.getFirmwareVersion(),
                m.getLatitude(),
                m.getLongitude(),
                m.getStatus());
    }
}
