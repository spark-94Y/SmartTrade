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

import com.TradeP.SmartTrade.entity.MeterReading;
import com.TradeP.SmartTrade.exception.BadRequestException;
import com.TradeP.SmartTrade.exception.ResourceNotFoundException;
import com.TradeP.SmartTrade.repository.MeterReadingRepository;

@RestController
public class MeterReadingController {
    private static final Logger logger = LoggerFactory.getLogger(MeterReadingController.class);

    @Autowired
    private MeterReadingRepository repo;

    @GetMapping("/meterreading")
    public List<MeterReading> getAllReadings() {
        logger.info("Fetching all meter readings");
        List<MeterReading> readings = repo.findAll();
        logger.info("Found {} meter readings", readings.size());
        return readings;
    }

    @GetMapping("/meterreading/{readingId}")
    public ResponseEntity<?> getReadingById(@PathVariable UUID readingId) {
        logger.info("Fetching meter reading with id={}", readingId);
        MeterReading reading = repo.findById(readingId)
                .orElseThrow(() -> new ResourceNotFoundException("Meter Reading", readingId));
        logger.info("Found meter reading: id={}", readingId);
        return ResponseEntity.ok(reading);
    }

    @GetMapping("/meterreading/meter/{meterId}")
    public ResponseEntity<?> getReadingsByMeterId(@PathVariable UUID meterId) {
        logger.info("Fetching meter readings for meterId={}", meterId);
        List<MeterReading> readings = repo.findByMeterIdOrderByTimestampDesc(meterId);
        logger.info("Found {} readings for meterId={}", readings.size(), meterId);
        return ResponseEntity.ok(readings);
    }

    @PostMapping("/createmeterreading")
    public ResponseEntity<?> createMeterReading(@RequestBody MeterReading reading) {
        logger.info("Creating new meter reading for meterId={}", reading.getMeterId());
        if (reading.getMeterId() == null || reading.getReadingValue() == null || reading.getReadingType() == null) {
            throw new BadRequestException("meterId, readingValue, and readingType are required");
        }

        MeterReading newReading = new MeterReading();
        newReading.setMeterId(reading.getMeterId());
        newReading.setReadingValue(reading.getReadingValue());
        newReading.setReadingType(reading.getReadingType());

        MeterReading savedReading = repo.save(newReading);
        logger.info("Meter reading created successfully with id={}", savedReading.getReadingId());
        return ResponseEntity.status(HttpStatus.CREATED).body(savedReading);
    }

    @PatchMapping("/meterreading/{readingId}")
    public ResponseEntity<?> updateMeterReading(@PathVariable UUID readingId, @RequestBody MeterReading incomingData) {
        logger.info("Updating meter reading with id={}", readingId);
        MeterReading existingReading = repo.findById(readingId)
                .orElseThrow(() -> new ResourceNotFoundException("Meter Reading", readingId));

        if (incomingData.getReadingValue() != null) {
            existingReading.setReadingValue(incomingData.getReadingValue());
        }
        if (incomingData.getReadingType() != null) {
            existingReading.setReadingType(incomingData.getReadingType());
        }

        MeterReading updatedReading = repo.save(existingReading);
        logger.info("Meter reading updated successfully: id={}", readingId);
        return ResponseEntity.ok(updatedReading);
    }

    @DeleteMapping("/meterreading/{readingId}")
    public ResponseEntity<?> deleteMeterReading(@PathVariable UUID readingId) {
        logger.info("Deleting meter reading with id={}", readingId);
        MeterReading reading = repo.findById(readingId)
                .orElseThrow(() -> new ResourceNotFoundException("Meter Reading", readingId));

        repo.delete(reading);
        logger.info("Meter reading deleted successfully: id={}", readingId);
        return ResponseEntity.ok("Meter Reading Deleted Successfully");
    }
}