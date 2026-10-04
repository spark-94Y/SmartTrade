package com.TradeP.SmartTrade.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.TradeP.SmartTrade.entity.House;

@Repository
public interface HouseRepository extends JpaRepository<House, UUID> {
    List<House> findByUser_UserId(UUID userId);
    List<House> findByUserId(UUID userId);
    List<House> findByGridZone_ZoneId(UUID zoneId);
}