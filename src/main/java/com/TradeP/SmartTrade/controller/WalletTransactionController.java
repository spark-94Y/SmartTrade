package com.TradeP.SmartTrade.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.TradeP.SmartTrade.entity.WalletTransaction;
import com.TradeP.SmartTrade.exception.BadRequestException;
import com.TradeP.SmartTrade.exception.ResourceNotFoundException;
import com.TradeP.SmartTrade.repository.WalletTransactionRepository;

@RestController
public class WalletTransactionController {
    private static final Logger logger = LoggerFactory.getLogger(WalletTransactionController.class);

    @Autowired
    private WalletTransactionRepository repo;

    @GetMapping("/wallettransaction")
    public List<WalletTransaction> getAllTransactions() {
        logger.info("Fetching all wallet transactions");
        List<WalletTransaction> transactions = repo.findAll();
        logger.info("Found {} wallet transactions", transactions.size());
        return transactions;
    }

    @GetMapping("/wallettransaction/{transactionId}")
    public ResponseEntity<?> getTransactionById(@PathVariable UUID transactionId) {
        logger.info("Fetching wallet transaction with id={}", transactionId);
        WalletTransaction transaction = repo.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction", transactionId));
        logger.info("Found wallet transaction: id={}", transactionId);
        return ResponseEntity.ok(transaction);
    }

    @GetMapping("/wallettransaction/user/{userId}")
    public ResponseEntity<?> getTransactionsByUserId(@PathVariable UUID userId) {
        logger.info("Fetching wallet transactions for userId={}", userId);
        List<WalletTransaction> transactions = repo.findByUser_UserId(userId);
        logger.info("Found {} transactions for userId={}", transactions.size(), userId);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/wallettransaction/trade/{tradeId}")
    public ResponseEntity<?> getTransactionsByTradeId(@PathVariable UUID tradeId) {
        logger.info("Fetching wallet transactions for tradeId={}", tradeId);
        List<WalletTransaction> transactions = repo.findByTrade_TradeId(tradeId);
        logger.info("Found {} transactions for tradeId={}", transactions.size(), tradeId);
        return ResponseEntity.ok(transactions);
    }

    @PostMapping("/createwallettransaction")
    public ResponseEntity<?> createTransaction(@RequestBody WalletTransaction tx) {
        logger.info("Creating new wallet transaction for userId={}", tx.getUserId());
        if (tx.getUserId() == null || tx.getAmount() == null || tx.getTransactionType() == null) {
            throw new BadRequestException("userId, amount, and transactionType are required");
        }

        if (tx.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Transaction amount must be greater than zero");
        }

        WalletTransaction newTx = new WalletTransaction();
        newTx.setUserId(tx.getUserId());
        newTx.setTradeId(tx.getTradeId());
        newTx.setAmount(tx.getAmount());
        newTx.setTransactionType(tx.getTransactionType().toUpperCase());
        newTx.setDescription(tx.getDescription());

        WalletTransaction savedTx = repo.save(newTx);
        logger.info("Wallet transaction created successfully with id={}", savedTx.getTransactionId());
        return ResponseEntity.status(HttpStatus.CREATED).body(savedTx);
    }

    @PatchMapping("/wallettransaction/{transactionId}")
    public ResponseEntity<?> updateTransaction(@PathVariable UUID transactionId, @RequestBody WalletTransaction incomingData) {
        logger.info("Updating wallet transaction with id={}", transactionId);
        WalletTransaction existingTx = repo.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction", transactionId));

        if (incomingData.getDescription() != null) {
            existingTx.setDescription(incomingData.getDescription());
        }

        WalletTransaction updatedTx = repo.save(existingTx);
        logger.info("Wallet transaction updated successfully: id={}", transactionId);
        return ResponseEntity.ok(updatedTx);
    }

    @DeleteMapping("/wallettransaction/{transactionId}")
    public ResponseEntity<?> deleteTransaction(@PathVariable UUID transactionId) {
        logger.info("Deleting wallet transaction with id={}", transactionId);
        WalletTransaction transaction = repo.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction", transactionId));

        repo.delete(transaction);
        logger.info("Wallet transaction deleted successfully: id={}", transactionId);
        return ResponseEntity.ok("Transaction Deleted Successfully");
    }
}