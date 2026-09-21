package com.TradeP.SmartTrade.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.TradeP.SmartTrade.entity.EnergyTrade;

@Repository
public interface EnergyTradeRepository extends JpaRepository<EnergyTrade, UUID> {

    List<EnergyTrade> findAllByOrderByExecutedAtDesc();

    /** Every trade in which the given order took part, as buy side or sell side. */
    @Query("select t from EnergyTrade t "
            + "where t.buyOrder.orderId = :orderId or t.sellOrder.orderId = :orderId "
            + "order by t.executedAt desc")
    List<EnergyTrade> findAllByOrderId(@Param("orderId") UUID orderId);

    /** Every trade in which the given user was the buyer or the seller. */
    @Query("select t from EnergyTrade t "
            + "where t.buyOrder.user.userId = :userId or t.sellOrder.user.userId = :userId "
            + "order by t.executedAt desc")
    List<EnergyTrade> findAllByParticipant(@Param("userId") UUID userId);
}
