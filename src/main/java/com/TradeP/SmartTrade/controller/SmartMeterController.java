package com.TradeP.SmartTrade.controller;

import java.util.List;
import java.util.UUID;

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
import org.springframework.web.server.ResponseStatusException;

import com.TradeP.SmartTrade.entity.SmartMeter;
import com.TradeP.SmartTrade.repository.SmartMeterRepository;

@RestController
@RequestMapping("/api/meters")
public class SmartMeterController {

    private final SmartMeterRepository repository;

    public SmartMeterController(SmartMeterRepository repository) {
        this.repository = repository;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SmartMeter createMeter(@RequestBody SmartMeter meter) {
        meter.setMeterId(null);
        return repository.save(meter);
    }

    @GetMapping
    public List<SmartMeter> getAllMeters() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public SmartMeter getMeter(@PathVariable UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Meter not found"));
    }

    @GetMapping("/house/{houseId}")
    public List<SmartMeter> getMetersByHouse(
            @PathVariable UUID houseId) {
        return repository.findByHouseId(houseId);
    }

    @PutMapping("/{id}")
    public SmartMeter updateMeter(
            @PathVariable UUID id,
            @RequestBody SmartMeter updatedMeter) {

        SmartMeter meter = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Meter not found"));

        meter.setMeterNumber(updatedMeter.getMeterNumber());
        meter.setHardwareVersion(updatedMeter.getHardwareVersion());
        meter.setFirmwareVersion(updatedMeter.getFirmwareVersion());
        meter.setInstallationDate(updatedMeter.getInstallationDate());
        meter.setStatus(updatedMeter.getStatus());

        return repository.save(meter);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMeter(@PathVariable UUID id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Meter not found");
        }

        repository.deleteById(id);
    }
}
