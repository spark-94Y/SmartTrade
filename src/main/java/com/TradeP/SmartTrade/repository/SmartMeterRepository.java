package com.TradeP.SmartTrade.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.TradeP.SmartTrade.entity.SmartMeter;

@Repository
public interface SmartMeterRepository extends JpaRepository<SmartMeter, UUID> {

    List<SmartMeter> findByUser_UserId(UUID userId);

    List<SmartMeter> findByZone_ZoneId(UUID zoneId);

    List<SmartMeter> findByStatus(String status);
}
