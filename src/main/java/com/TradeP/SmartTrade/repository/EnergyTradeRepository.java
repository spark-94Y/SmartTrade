package com.TradeP.SmartTrade.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.TradeP.SmartTrade.entity.EnergyTrade;

@Repository
public interface EnergyTradeRepository extends JpaRepository<EnergyTrade, UUID> {
    List<EnergyTrade> findByBuyer_UserId(UUID buyerId);
    List<EnergyTrade> findByBuyerId(UUID buyerId);
    List<EnergyTrade> findBySeller_UserId(UUID sellerId);
    List<EnergyTrade> findBySellerId(UUID sellerId);
    List<EnergyTrade> findByBuyOrder_OrderId(UUID buyOrderId);
    List<EnergyTrade> findByBuyOrderId(UUID buyOrderId);
    List<EnergyTrade> findBySellOrder_OrderId(UUID sellOrderId);
    List<EnergyTrade> findBySellOrderId(UUID sellOrderId);
    List<EnergyTrade> findByStatus(String status);
}