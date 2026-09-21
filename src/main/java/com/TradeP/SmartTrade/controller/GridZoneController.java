package com.TradeP.SmartTrade.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.TradeP.SmartTrade.entity.GridZone;
import com.TradeP.SmartTrade.repository.GridZoneRepository;

@RestController
@RequestMapping("/api/zones")
public class GridZoneController {

    private final GridZoneRepository repository;

    public GridZoneController(GridZoneRepository repository) {
        this.repository = repository;
    }

    // ---------------------------------------------------------------- CREATE

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GridZone createZone(@RequestBody GridZone zone) {
        zone.setZoneId(null); // id is always generated, never taken from the client
        normalize(zone);
        validate(zone);

        if (repository.existsByZoneNameIgnoreCase(zone.getZoneName())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "A zone named '" + zone.getZoneName() + "' already exists");
        }
        return repository.save(zone);
    }

    // ------------------------------------------------------------------ READ

    /**
     * GET /api/zones
     * GET /api/zones?status=CONGESTED
     * GET /api/zones?weather=CLOUDY
     */
    @GetMapping
    public List<GridZone> getZones(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String weather) {

        if (status != null && !status.isBlank()) {
            return repository.findByGridStatusIgnoreCase(status.trim());
        }
        if (weather != null && !weather.isBlank()) {
            return repository.findByWeatherConditionIgnoreCase(weather.trim());
        }
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public GridZone getZone(@PathVariable UUID id) {
        return findOrThrow(id);
    }

    @GetMapping("/name/{zoneName}")
    public GridZone getZoneByName(@PathVariable String zoneName) {
        return repository.findByZoneNameIgnoreCase(zoneName.trim())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Zone not found"));
    }

    // ---------------------------------------------------------------- UPDATE

    /** Full replace: fields left out of the body fall back to their defaults. */
    @PutMapping("/{id}")
    public GridZone updateZone(@PathVariable UUID id, @RequestBody GridZone updated) {
        GridZone zone = findOrThrow(id);

        normalize(updated);
        validate(updated);

        if (repository.existsByZoneNameIgnoreCaseAndZoneIdNot(updated.getZoneName(), id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "A zone named '" + updated.getZoneName() + "' already exists");
        }

        zone.setZoneName(updated.getZoneName());
        zone.setCurrentSolarIrradiance(updated.getCurrentSolarIrradiance());
        zone.setWeatherCondition(updated.getWeatherCondition());
        zone.setTotalAggregateReserveKwh(updated.getTotalAggregateReserveKwh());
        zone.setGridStatus(updated.getGridStatus());
        zone.setBaseMarketClearingPrice(updated.getBaseMarketClearingPrice());

        return repository.save(zone);
    }

    /**
     * Partial update: only the fields present (non-null) in the body are changed.
     * Handy for a simulator / pricing engine pushing live context, e.g.
     * {"currentSolarIrradiance": 640.50, "weatherCondition": "CLOUDY"}
     */
    @PatchMapping("/{id}")
    public GridZone patchZone(@PathVariable UUID id, @RequestBody GridZone patch) {
        GridZone zone = findOrThrow(id);

        if (patch.getZoneName() != null) {
            String name = patch.getZoneName().trim();
            if (name.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "zoneName must not be blank");
            }
            if (repository.existsByZoneNameIgnoreCaseAndZoneIdNot(name, id)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "A zone named '" + name + "' already exists");
            }
            zone.setZoneName(name);
        }
        if (patch.getCurrentSolarIrradiance() != null) {
            zone.setCurrentSolarIrradiance(patch.getCurrentSolarIrradiance());
        }
        if (patch.getWeatherCondition() != null) {
            zone.setWeatherCondition(patch.getWeatherCondition().trim());
        }
        if (patch.getTotalAggregateReserveKwh() != null) {
            zone.setTotalAggregateReserveKwh(patch.getTotalAggregateReserveKwh());
        }
        if (patch.getGridStatus() != null) {
            zone.setGridStatus(patch.getGridStatus().trim().toUpperCase());
        }
        if (patch.getBaseMarketClearingPrice() != null) {
            zone.setBaseMarketClearingPrice(patch.getBaseMarketClearingPrice());
        }

        validate(zone);
        return repository.save(zone);
    }

    // ---------------------------------------------------------------- DELETE

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteZone(@PathVariable UUID id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Zone not found");
        }
        repository.deleteById(id);
    }

    // --------------------------------------------------------------- HELPERS

    private GridZone findOrThrow(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Zone not found"));
    }

    /** Trim text, upper-case the status, and replace missing values with defaults. */
    private void normalize(GridZone zone) {
        if (zone.getZoneName() != null) {
            zone.setZoneName(zone.getZoneName().trim());
        }
        if (zone.getCurrentSolarIrradiance() == null) {
            zone.setCurrentSolarIrradiance(BigDecimal.ZERO);
        }
        if (zone.getWeatherCondition() == null || zone.getWeatherCondition().isBlank()) {
            zone.setWeatherCondition("UNKNOWN");
        } else {
            zone.setWeatherCondition(zone.getWeatherCondition().trim());
        }
        if (zone.getTotalAggregateReserveKwh() == null) {
            zone.setTotalAggregateReserveKwh(BigDecimal.ZERO);
        }
        if (zone.getGridStatus() == null || zone.getGridStatus().isBlank()) {
            zone.setGridStatus("STABLE");
        } else {
            zone.setGridStatus(zone.getGridStatus().trim().toUpperCase());
        }
        if (zone.getBaseMarketClearingPrice() == null) {
            zone.setBaseMarketClearingPrice(BigDecimal.ZERO);
        }
    }

    private void validate(GridZone zone) {
        if (zone.getZoneName() == null || zone.getZoneName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "zoneName is required");
        }
        if (zone.getZoneName().length() > 100) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "zoneName must be at most 100 characters");
        }
        requireNonNegative(zone.getCurrentSolarIrradiance(), "currentSolarIrradiance");
        requireNonNegative(zone.getTotalAggregateReserveKwh(), "totalAggregateReserveKwh");
        requireNonNegative(zone.getBaseMarketClearingPrice(), "baseMarketClearingPrice");
    }

    private void requireNonNegative(BigDecimal value, String field) {
        if (value != null && value.signum() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, field + " must not be negative");
        }
    }
}
