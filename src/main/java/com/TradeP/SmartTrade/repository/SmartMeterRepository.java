package com.TradeP.SmartTrade.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.TradeP.SmartTrade.entity.SmartMeter;

public interface SmartMeterRepository
        extends JpaRepository<SmartMeter, UUID> {

    Optional<SmartMeter> findByMeterNumber(String meterNumber);

    List<SmartMeter> findByHouseId(UUID houseId);

    List<SmartMeter> findByStatus(String status);
}
