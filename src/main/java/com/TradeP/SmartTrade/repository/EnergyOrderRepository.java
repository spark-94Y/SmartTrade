package com.TradeP.SmartTrade.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.TradeP.SmartTrade.entity.EnergyOrder;

public interface EnergyOrderRepository extends JpaRepository<EnergyOrder, UUID> {
    List<EnergyOrder> findByUser_UserId(UUID userId);
}
