package com.TradeP.SmartTrade.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.TradeP.SmartTrade.entity.EnergyTrade;

public interface EnergyTradeRepository extends JpaRepository<EnergyTrade, UUID> {
    List<EnergyTrade> findByBuyerId(UUID buyerId);
    List<EnergyTrade> findBySellerId(UUID sellerId);
    List<EnergyTrade> findByBuyOrderId(UUID buyOrderId);
    List<EnergyTrade> findBySellOrderId(UUID sellOrderId);
    List<EnergyTrade> findByStatus(String status);
}