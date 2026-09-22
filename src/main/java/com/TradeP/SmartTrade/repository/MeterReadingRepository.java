package com.TradeP.SmartTrade.repository;

import com.TradeP.SmartTrade.entity.MeterReading;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MeterReadingRepository extends JpaRepository<MeterReading, UUID> {

    // Fetch all meter readings for a specific smart meter
    List<MeterReading> findByMeterId(UUID meterId);

    // Fetch meter readings for a specific smart meter ordered by timestamp descending
    List<MeterReading> findByMeterIdOrderByTimestampDesc(UUID meterId);
}

