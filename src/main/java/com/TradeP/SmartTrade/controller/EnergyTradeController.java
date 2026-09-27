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

import com.TradeP.SmartTrade.entity.EnergyTrade;
import com.TradeP.SmartTrade.repository.EnergyTradeRepository;

@RestController
public class EnergyTradeController {

    @Autowired
    private EnergyTradeRepository repo;

    @GetMapping("/energytrade")
    public List<EnergyTrade> getAllTrades() {
        return repo.findAll();
    }

    @GetMapping("/energytrade/{tradeId}")
    public ResponseEntity<?> getTradeById(@PathVariable UUID tradeId) {
        Optional<EnergyTrade> optionalTrade = repo.findById(tradeId);
        if (optionalTrade.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Energy Trade Not Found");
        }
        return ResponseEntity.ok(optionalTrade.get());
    }

    @GetMapping("/energytrade/buyer/{buyerId}")
    public ResponseEntity<?> getTradesByBuyerId(@PathVariable UUID buyerId) {
        List<EnergyTrade> trades = repo.findByBuyerId(buyerId);
        return ResponseEntity.ok(trades);
    }

    @GetMapping("/energytrade/seller/{sellerId}")
    public ResponseEntity<?> getTradesBySellerId(@PathVariable UUID sellerId) {
        List<EnergyTrade> trades = repo.findBySellerId(sellerId);
        return ResponseEntity.ok(trades);
    }

    @PostMapping("/createenergytrade")
    public ResponseEntity<?> createTrade(@RequestBody EnergyTrade trade) {
        if (trade.getBuyOrderId() == null || trade.getSellOrderId() == null ||
            trade.getBuyerId() == null || trade.getSellerId() == null ||
            trade.getClearedQuantityKwh() == null || trade.getClearedPricePerKwh() == null) {
            return ResponseEntity.badRequest().body("Orders, parties, quantity, and price are required");
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

        return ResponseEntity.status(HttpStatus.CREATED).body(repo.save(newTrade));
    }

    @PatchMapping("/energytrade/{tradeId}")
    public ResponseEntity<?> updateTradeStatus(@PathVariable UUID tradeId, @RequestBody EnergyTrade incomingData) {
        Optional<EnergyTrade> optionalTrade = repo.findById(tradeId);
        if (optionalTrade.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Energy Trade Not Found");
        }

        EnergyTrade existingTrade = optionalTrade.get();

        if (incomingData.getStatus() != null) {
            existingTrade.setStatus(incomingData.getStatus());
        }

        return ResponseEntity.ok(repo.save(existingTrade));
    }

    @DeleteMapping("/energytrade/{tradeId}")
    public ResponseEntity<?> deleteTrade(@PathVariable UUID tradeId) {
        Optional<EnergyTrade> optionalTrade = repo.findById(tradeId);
        if (optionalTrade.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Energy Trade Not Found");
        }

        repo.delete(optionalTrade.get());
        return ResponseEntity.ok("Energy Trade Deleted Successfully");
    }
}