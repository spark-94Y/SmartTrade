package com.TradeP.SmartTrade.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.TradeP.SmartTrade.entity.EnergyOrder;

@Repository
public interface EnergyOrderRepository extends JpaRepository<EnergyOrder, UUID> {
    List<EnergyOrder> findByUser_UserId(UUID userId);
    List<EnergyOrder> findByMeter_MeterId(UUID meterId);
}
