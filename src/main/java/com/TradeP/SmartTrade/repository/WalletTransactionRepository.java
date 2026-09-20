package com.TradeP.SmartTrade.repository;

import com.TradeP.SmartTrade.entity.WalletTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, UUID> {
    
    // Fetch all financial history/audit logs for a specific user
    List<WalletTransaction> findByUserId(UUID userId);
    
    // Fetch transactions linked to a specific energy trade settlement
    List<WalletTransaction> findByTradeId(UUID tradeId);
}