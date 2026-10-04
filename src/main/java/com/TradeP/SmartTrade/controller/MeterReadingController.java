package com.TradeP.SmartTrade.controller;

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

import com.TradeP.SmartTrade.entity.MeterReading;
import com.TradeP.SmartTrade.repository.MeterReadingRepository;

@RestController
public class MeterReadingController {

    @Autowired
    private MeterReadingRepository repo;

    @GetMapping("/meterreading")
    public List<MeterReading> getAllReadings() {
        return repo.findAll();
    }

    @GetMapping("/meterreading/{readingId}")
    public ResponseEntity<?> getReadingById(@PathVariable UUID readingId) {
        Optional<MeterReading> optionalReading = repo.findById(readingId);
        if (optionalReading.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Meter Reading Not Found");
        }
        return ResponseEntity.ok(optionalReading.get());
    }

    @GetMapping("/meterreading/meter/{meterId}")
    public ResponseEntity<?> getReadingsByMeterId(@PathVariable UUID meterId) {
        List<MeterReading> readings = repo.findByMeterIdOrderByTimestampDesc(meterId);
        return ResponseEntity.ok(readings);
    }

    @PostMapping("/createmeterreading")
    public ResponseEntity<?> createMeterReading(@RequestBody MeterReading reading) {
        if (reading.getMeterId() == null || reading.getReadingValue() == null || reading.getReadingType() == null) {
            return ResponseEntity.badRequest().body("meterId, readingValue, and readingType are required");
        }

        MeterReading newReading = new MeterReading();
        newReading.setMeterId(reading.getMeterId());
        newReading.setReadingValue(reading.getReadingValue());
        newReading.setReadingType(reading.getReadingType());

        return ResponseEntity.status(HttpStatus.CREATED).body(repo.save(newReading));
    }

    @PatchMapping("/meterreading/{readingId}")
    public ResponseEntity<?> updateMeterReading(@PathVariable UUID readingId, @RequestBody MeterReading incomingData) {
        Optional<MeterReading> optionalReading = repo.findById(readingId);
        if (optionalReading.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Meter Reading Not Found");
        }

        MeterReading existingReading = optionalReading.get();

        if (incomingData.getReadingValue() != null) {
            existingReading.setReadingValue(incomingData.getReadingValue());
        }
        if (incomingData.getReadingType() != null) {
            existingReading.setReadingType(incomingData.getReadingType());
        }

        return ResponseEntity.ok(repo.save(existingReading));
    }

    @DeleteMapping("/meterreading/{readingId}")
    public ResponseEntity<?> deleteMeterReading(@PathVariable UUID readingId) {
        Optional<MeterReading> optionalReading = repo.findById(readingId);
        if (optionalReading.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Meter Reading Not Found");
        }

        repo.delete(optionalReading.get());
        return ResponseEntity.ok("Meter Reading Deleted Successfully");
    }
}