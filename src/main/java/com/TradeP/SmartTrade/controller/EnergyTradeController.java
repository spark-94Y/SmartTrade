package com.TradeP.SmartTrade.controller;

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

import com.TradeP.SmartTrade.entity.EnergyTrade;
import com.TradeP.SmartTrade.exception.BadRequestException;
import com.TradeP.SmartTrade.exception.ResourceNotFoundException;
import com.TradeP.SmartTrade.repository.EnergyTradeRepository;

@RestController
public class EnergyTradeController {
    private static final Logger logger = LoggerFactory.getLogger(EnergyTradeController.class);

    @Autowired
    private EnergyTradeRepository repo;

    @GetMapping("/energytrade")
    public List<EnergyTrade> getAllTrades() {
        logger.info("Fetching all energy trades");
        List<EnergyTrade> trades = repo.findAll();
        logger.info("Found {} energy trades", trades.size());
        return trades;
    }

    @GetMapping("/energytrade/{tradeId}")
    public ResponseEntity<?> getTradeById(@PathVariable UUID tradeId) {
        logger.info("Fetching energy trade with id={}", tradeId);
        EnergyTrade trade = repo.findById(tradeId)
                .orElseThrow(() -> new ResourceNotFoundException("Energy Trade", tradeId));
        logger.info("Found energy trade: id={}", tradeId);
        return ResponseEntity.ok(trade);
    }

    @GetMapping("/energytrade/buyer/{buyerId}")
    public ResponseEntity<?> getTradesByBuyerId(@PathVariable UUID buyerId) {
        logger.info("Fetching trades for buyerId={}", buyerId);
        List<EnergyTrade> trades = repo.findByBuyer_UserId(buyerId);
        logger.info("Found {} trades for buyerId={}", trades.size(), buyerId);
        return ResponseEntity.ok(trades);
    }

    @GetMapping("/energytrade/seller/{sellerId}")
    public ResponseEntity<?> getTradesBySellerId(@PathVariable UUID sellerId) {
        logger.info("Fetching trades for sellerId={}", sellerId);
        List<EnergyTrade> trades = repo.findBySeller_UserId(sellerId);
        logger.info("Found {} trades for sellerId={}", trades.size(), sellerId);
        return ResponseEntity.ok(trades);
    }

    @PostMapping("/createenergytrade")
    public ResponseEntity<?> createTrade(@RequestBody EnergyTrade trade) {
        logger.info("Creating new energy trade");
        if (trade.getBuyOrderId() == null || trade.getSellOrderId() == null ||
            trade.getBuyerId() == null || trade.getSellerId() == null ||
            trade.getClearedQuantityKwh() == null || trade.getClearedPricePerKwh() == null) {
            throw new BadRequestException("Orders, parties, quantity, and price are required");
        }

        EnergyTrade newTrade = new EnergyTrade();
        newTrade.setBuyOrderId(trade.getBuyOrderId());
        newTrade.setSellOrderId(trade.getSellOrderId());
        newTrade.setBuyerId(trade.getBuyerId());
        newTrade.setSellerId(trade.getSellerId());
        newTrade.setClearedQuantityKwh(trade.getClearedQuantityKwh());
        newTrade.setClearedPricePerKwh(trade.getClearedPricePerKwh());

        if (trade.getTotalCost() != null) {
            newTrade.setTotalCost(trade.getTotalCost());
        } else {
            newTrade.setTotalCost(trade.getClearedQuantityKwh().multiply(trade.getClearedPricePerKwh()));
        }

        if (trade.getStatus() != null) {
            newTrade.setStatus(trade.getStatus());
        }

        EnergyTrade savedTrade = repo.save(newTrade);
        logger.info("Energy trade created successfully with id={}", savedTrade.getTradeId());
        return ResponseEntity.status(HttpStatus.CREATED).body(savedTrade);
    }

    @PatchMapping("/energytrade/{tradeId}")
    public ResponseEntity<?> updateTradeStatus(@PathVariable UUID tradeId, @RequestBody EnergyTrade incomingData) {
        logger.info("Updating energy trade with id={}", tradeId);
        EnergyTrade existingTrade = repo.findById(tradeId)
                .orElseThrow(() -> new ResourceNotFoundException("Energy Trade", tradeId));

        if (incomingData.getStatus() != null) {
            existingTrade.setStatus(incomingData.getStatus());
        }

        EnergyTrade updatedTrade = repo.save(existingTrade);
        logger.info("Energy trade updated successfully: id={}", tradeId);
        return ResponseEntity.ok(updatedTrade);
    }

    @DeleteMapping("/energytrade/{tradeId}")
    public ResponseEntity<?> deleteTrade(@PathVariable UUID tradeId) {
        logger.info("Deleting energy trade with id={}", tradeId);
        EnergyTrade trade = repo.findById(tradeId)
                .orElseThrow(() -> new ResourceNotFoundException("Energy Trade", tradeId));

        repo.delete(trade);
        logger.info("Energy trade deleted successfully: id={}", tradeId);
        return ResponseEntity.ok("Energy Trade Deleted Successfully");
    }
}