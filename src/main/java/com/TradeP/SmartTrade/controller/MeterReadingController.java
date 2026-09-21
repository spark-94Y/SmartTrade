package com.TradeP.SmartTrade.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.TradeP.SmartTrade.dto.MeterReadingRequest;
import com.TradeP.SmartTrade.dto.MeterReadingResponse;
import com.TradeP.SmartTrade.entity.MeterReading;
import com.TradeP.SmartTrade.entity.SmartMeter;
import com.TradeP.SmartTrade.repository.MeterReadingRepository;
import com.TradeP.SmartTrade.repository.SmartMeterRepository;

/**
 * Meter readings are append-only, so there is no PUT / PATCH / DELETE here.
 */
@RestController
@RequestMapping("/api/readings")
public class MeterReadingController {

    private static final BigDecimal HUNDRED = new BigDecimal("100");

    private final MeterReadingRepository readingRepo;
    private final SmartMeterRepository meterRepo;

    public MeterReadingController(MeterReadingRepository readingRepo, SmartMeterRepository meterRepo) {
        this.readingRepo = readingRepo;
        this.meterRepo = meterRepo;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MeterReadingResponse createReading(@RequestBody MeterReadingRequest req) {
        if (req.meterId() == null) {
            throw bad("meterId is required");
        }
        if (req.currentGenerationKwh() == null || req.currentGenerationKwh().signum() < 0) {
            throw bad("currentGenerationKwh is required and cannot be negative");
        }
        if (req.currentConsumptionKwh() == null || req.currentConsumptionKwh().signum() < 0) {
            throw bad("currentConsumptionKwh is required and cannot be negative");
        }
        if (req.batterySocPercentage() != null
                && (req.batterySocPercentage().signum() < 0 || req.batterySocPercentage().compareTo(HUNDRED) > 0)) {
            throw bad("batterySocPercentage must be between 0 and 100");
        }

        SmartMeter meter = meterRepo.findById(req.meterId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Meter not found"));

        BigDecimal generation = req.currentGenerationKwh().setScale(4, RoundingMode.HALF_UP);
        BigDecimal consumption = req.currentConsumptionKwh().setScale(4, RoundingMode.HALF_UP);
        BigDecimal netFlow = req.netGridFlowKwh() != null
                ? req.netGridFlowKwh().setScale(4, RoundingMode.HALF_UP)
                : generation.subtract(consumption);

        MeterReading reading = new MeterReading();
        reading.setMeter(meter);
        reading.setTimestamp(req.timestamp() != null ? req.timestamp() : LocalDateTime.now());
        reading.setCurrentGenerationKwh(generation);
        reading.setCurrentConsumptionKwh(consumption);
        reading.setNetGridFlowKwh(netFlow);
        if (req.batterySocPercentage() != null) {
            reading.setBatterySocPercentage(req.batterySocPercentage().setScale(2, RoundingMode.HALF_UP));
        }
        return MeterReadingResponse.from(readingRepo.save(reading));
    }

    @GetMapping("/{readingId}")
    public MeterReadingResponse getReading(@PathVariable UUID readingId) {
        return readingRepo.findById(readingId)
                .map(MeterReadingResponse::from)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reading not found"));
    }

    /** All readings of a meter, newest first. Add ?from=...&to=... (ISO date-time) for a window, oldest first. */
    @GetMapping("/meter/{meterId}")
    public List<MeterReadingResponse> getReadingsByMeter(
            @PathVariable UUID meterId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {

        requireMeter(meterId);
        List<MeterReading> readings;
        if (from != null && to != null) {
            readings = readingRepo.findByMeter_MeterIdAndTimestampBetweenOrderByTimestampAsc(meterId, from, to);
        } else if (from == null && to == null) {
            readings = readingRepo.findByMeter_MeterIdOrderByTimestampDesc(meterId);
        } else {
            throw bad("Send both from and to, or neither");
        }
        return readings.stream().map(MeterReadingResponse::from).toList();
    }

    @GetMapping("/meter/{meterId}/latest")
    public MeterReadingResponse getLatestReading(@PathVariable UUID meterId) {
        requireMeter(meterId);
        return readingRepo.findFirstByMeter_MeterIdOrderByTimestampDesc(meterId)
                .map(MeterReadingResponse::from)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No readings for this meter yet"));
    }

    private void requireMeter(UUID meterId) {
        if (!meterRepo.existsById(meterId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Meter not found");
        }
    }

    private static ResponseStatusException bad(String message) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }
}
