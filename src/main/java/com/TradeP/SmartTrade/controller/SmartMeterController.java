package com.TradeP.SmartTrade.controller;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.TradeP.SmartTrade.entity.SmartMeter;
import com.TradeP.SmartTrade.exception.ResourceNotFoundException;
import com.TradeP.SmartTrade.repository.SmartMeterRepository;

@RestController
@RequestMapping("/api/meters")
public class SmartMeterController {
    private static final Logger logger = LoggerFactory.getLogger(SmartMeterController.class);

    private final SmartMeterRepository repository;

    public SmartMeterController(SmartMeterRepository repository) {
        this.repository = repository;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SmartMeter createMeter(@RequestBody SmartMeter meter) {
        logger.info("Creating new smart meter");
        meter.setMeterId(null);
        SmartMeter savedMeter = repository.save(meter);
        logger.info("Smart meter created successfully with id={}", savedMeter.getMeterId());
        return savedMeter;
    }

    @GetMapping
    public List<SmartMeter> getAllMeters() {
        logger.info("Fetching all smart meters");
        List<SmartMeter> meters = repository.findAll();
        logger.info("Found {} smart meters", meters.size());
        return meters;
    }

    @GetMapping("/{id}")
    public SmartMeter getMeter(@PathVariable UUID id) {
        logger.info("Fetching smart meter with id={}", id);
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Meter", id));
    }

    @GetMapping("/house/{houseId}")
    public List<SmartMeter> getMetersByHouse(@PathVariable UUID houseId) {
        logger.info("Fetching smart meters for houseId={}", houseId);
        List<SmartMeter> meters = repository.findByHouse_HouseId(houseId);
        logger.info("Found {} meters for houseId={}", meters.size(), houseId);
        return meters;
    }

    @PutMapping("/{id}")
    public SmartMeter updateMeter(
            @PathVariable UUID id,
            @RequestBody SmartMeter updatedMeter) {

        logger.info("Updating smart meter with id={}", id);
        SmartMeter meter = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Meter", id));

        meter.setMeterNumber(updatedMeter.getMeterNumber());
        meter.setHardwareVersion(updatedMeter.getHardwareVersion());
        meter.setFirmwareVersion(updatedMeter.getFirmwareVersion());
        meter.setInstallationDate(updatedMeter.getInstallationDate());
        meter.setStatus(updatedMeter.getStatus());

        SmartMeter savedMeter = repository.save(meter);
        logger.info("Smart meter updated successfully: id={}", id);
        return savedMeter;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMeter(@PathVariable UUID id) {
        logger.info("Deleting smart meter with id={}", id);
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Meter", id);
        }

        repository.deleteById(id);
        logger.info("Smart meter deleted successfully: id={}", id);
    }
}
