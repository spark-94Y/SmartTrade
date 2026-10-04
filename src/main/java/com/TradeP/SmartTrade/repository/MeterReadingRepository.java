package com.TradeP.SmartTrade.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.TradeP.SmartTrade.entity.MeterReading;

@Repository
public interface MeterReadingRepository extends JpaRepository<MeterReading, UUID> {
    List<MeterReading> findByMeter_MeterId(UUID meterId);
    List<MeterReading> findByMeterId(UUID meterId);
    List<MeterReading> findByMeter_MeterIdOrderByTimestampDesc(UUID meterId);
    List<MeterReading> findByMeterIdOrderByTimestampDesc(UUID meterId);
}