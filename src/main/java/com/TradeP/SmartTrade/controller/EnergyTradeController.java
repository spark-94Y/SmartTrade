package com.TradeP.SmartTrade.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.TradeP.SmartTrade.dto.TradeRequest;
import com.TradeP.SmartTrade.dto.TradeResponse;
import com.TradeP.SmartTrade.service.EnergyTradeService;

/**
 * Trades are an immutable ledger, so there is no PUT / PATCH / DELETE here.
 */
@RestController
@RequestMapping("/api/trades")
public class EnergyTradeController {

    private final EnergyTradeService service;

    public EnergyTradeController(EnergyTradeService service) {
        this.service = service;
    }

    /** Match a BUY order with a SELL order. */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TradeResponse executeTrade(@RequestBody TradeRequest request) {
        return service.executeTrade(request);
    }

    @GetMapping
    public List<TradeResponse> getAllTrades() {
        return service.listAll();
    }

    @GetMapping("/{tradeId}")
    public TradeResponse getTrade(@PathVariable UUID tradeId) {
        return service.getTrade(tradeId);
    }

    /** Trades where the user was buyer or seller. */
    @GetMapping("/user/{userId}")
    public List<TradeResponse> getTradesByUser(@PathVariable UUID userId) {
        return service.listForUser(userId);
    }

    /** Trades that filled (part of) the given order. */
    @GetMapping("/order/{orderId}")
    public List<TradeResponse> getTradesByOrder(@PathVariable UUID orderId) {
        return service.listForOrder(orderId);
    }
}
