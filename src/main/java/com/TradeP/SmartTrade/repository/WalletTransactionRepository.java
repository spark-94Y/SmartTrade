package com.TradeP.SmartTrade.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.TradeP.SmartTrade.entity.WalletTransaction;

@Repository
public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, UUID> {
    List<WalletTransaction> findByUser_UserId(UUID userId);
    List<WalletTransaction> findByUserId(UUID userId);
    List<WalletTransaction> findByTrade_TradeId(UUID tradeId);
    List<WalletTransaction> findByTradeId(UUID tradeId);
}