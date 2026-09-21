package com.TradeP.SmartTrade.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.TradeP.SmartTrade.entity.House;

public interface HouseRepository extends JpaRepository<House, UUID> {
    List<House> findByUserId(UUID userId);
    List<House> findByZoneId(Long zoneId);
}