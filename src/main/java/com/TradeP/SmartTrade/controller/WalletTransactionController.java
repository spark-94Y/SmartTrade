package com.TradeP.SmartTrade.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
import com.TradeP.SmartTrade.repository.WalletTransactionRepository;

@RestController
public class WalletTransactionController {

    @Autowired
    private WalletTransactionRepository repo;

    @GetMapping("/wallettransaction")
    public List<WalletTransaction> getAllTransactions() {
        return repo.findAll();
    }

    @GetMapping("/wallettransaction/{transactionId}")
    public ResponseEntity<?> getTransactionById(@PathVariable UUID transactionId) {
        Optional<WalletTransaction> optionalTransaction = repo.findById(transactionId);
        if (optionalTransaction.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Transaction Not Found");
        }
        return ResponseEntity.ok(optionalTransaction.get());
    }

    @GetMapping("/wallettransaction/user/{userId}")
    public ResponseEntity<?> getTransactionsByUserId(@PathVariable UUID userId) {
        List<WalletTransaction> transactions = repo.findByUserId(userId);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/wallettransaction/trade/{tradeId}")
    public ResponseEntity<?> getTransactionsByTradeId(@PathVariable UUID tradeId) {
        List<WalletTransaction> transactions = repo.findByTradeId(tradeId);
        return ResponseEntity.ok(transactions);
    }

    @PostMapping("/createwallettransaction")
    public ResponseEntity<?> createTransaction(@RequestBody WalletTransaction tx) {
        if (tx.getUserId() == null || tx.getAmount() == null || tx.getTransactionType() == null) {
            return ResponseEntity.badRequest().body("userId, amount, and transactionType are required");
        }

        if (tx.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return ResponseEntity.badRequest().body("Transaction amount must be greater than zero");
        }

        WalletTransaction newTx = new WalletTransaction();
        newTx.setUserId(tx.getUserId());
        newTx.setTradeId(tx.getTradeId());
        newTx.setAmount(tx.getAmount());
        newTx.setTransactionType(tx.getTransactionType().toUpperCase());
        newTx.setDescription(tx.getDescription());

        return ResponseEntity.status(HttpStatus.CREATED).body(repo.save(newTx));
    }

    @PatchMapping("/wallettransaction/{transactionId}")
    public ResponseEntity<?> updateTransaction(@PathVariable UUID transactionId, @RequestBody WalletTransaction incomingData) {
        Optional<WalletTransaction> optionalTransaction = repo.findById(transactionId);
        if (optionalTransaction.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Transaction Not Found");
        }

        WalletTransaction existingTx = optionalTransaction.get();

        if (incomingData.getDescription() != null) {
            existingTx.setDescription(incomingData.getDescription());
        }

        return ResponseEntity.ok(repo.save(existingTx));
    }

    @DeleteMapping("/wallettransaction/{transactionId}")
    public ResponseEntity<?> deleteTransaction(@PathVariable UUID transactionId) {
        Optional<WalletTransaction> optionalTransaction = repo.findById(transactionId);
        if (optionalTransaction.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Transaction Not Found");
        }

        repo.delete(optionalTransaction.get());
        return ResponseEntity.ok("Transaction Deleted Successfully");
    }
}