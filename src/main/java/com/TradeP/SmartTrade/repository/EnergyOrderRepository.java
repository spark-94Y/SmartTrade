package com.TradeP.SmartTrade.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.TradeP.SmartTrade.entity.EnergyOrder;

import jakarta.persistence.LockModeType;

public interface EnergyOrderRepository extends JpaRepository<EnergyOrder, UUID> {

    List<EnergyOrder> findByUser_UserId(UUID userId);

    List<EnergyOrder> findByMeter_MeterId(UUID meterId);

    List<EnergyOrder> findByStatus(String status);

    boolean existsByMeter_MeterId(UUID meterId);

    /**
     * Loads an order and takes a row lock (SELECT ... FOR UPDATE) until the
     * surrounding transaction ends. Used when executing a trade so two trades
     * cannot fill the same order at the same time.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select o from EnergyOrder o where o.orderId = :orderId")
    Optional<EnergyOrder> findByIdForUpdate(@Param("orderId") UUID orderId);
}
