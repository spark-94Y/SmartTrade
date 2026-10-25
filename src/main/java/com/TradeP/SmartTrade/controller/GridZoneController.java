package com.TradeP.SmartTrade.controller;

// merged with remote main - keeping complete implementation
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import com.TradeP.SmartTrade.entity.GridZone;
import com.TradeP.SmartTrade.exception.BadRequestException;
import com.TradeP.SmartTrade.exception.DuplicateResourceException;
import com.TradeP.SmartTrade.exception.ResourceNotFoundException;
import com.TradeP.SmartTrade.repository.GridZoneRepository;

@RestController
@RequestMapping("/api/zones")
public class GridZoneController {
    private static final Logger logger = LoggerFactory.getLogger(GridZoneController.class);

    private final GridZoneRepository repository;

    public GridZoneController(GridZoneRepository repository) {
        this.repository = repository;
    }

    // ---------------------------------------------------------------- CREATE

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GridZone createZone(@RequestBody GridZone zone) {
        logger.info("Creating new grid zone with name={}", zone.getZoneName());
        zone.setZoneId(null); // id is always generated, never taken from the client
        normalize(zone);
        validate(zone);

        if (repository.existsByZoneNameIgnoreCase(zone.getZoneName())) {
            throw new DuplicateResourceException(
                    "A zone named '" + zone.getZoneName() + "' already exists");
        }
        GridZone savedZone = repository.save(zone);
        logger.info("Grid zone created successfully with id={}", savedZone.getZoneId());
        return savedZone;
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

        logger.info("Fetching grid zones with status={}, weather={}", status, weather);
        List<GridZone> zones;
        if (status != null && !status.isBlank()) {
            zones = repository.findByGridStatusIgnoreCase(status.trim());
        } else if (weather != null && !weather.isBlank()) {
            zones = repository.findByWeatherConditionIgnoreCase(weather.trim());
        } else {
            zones = repository.findAll();
        }
        logger.info("Found {} grid zones", zones.size());
        return zones;
    }

    @GetMapping("/{id}")
    public GridZone getZone(@PathVariable UUID id) {
        logger.info("Fetching grid zone with id={}", id);
        return findOrThrow(id);
    }

    @GetMapping("/name/{zoneName}")
    public GridZone getZoneByName(@PathVariable String zoneName) {
        logger.info("Fetching grid zone with name={}", zoneName);
        return repository.findByZoneNameIgnoreCase(zoneName.trim())
                .orElseThrow(() -> new ResourceNotFoundException("Zone not found with name: " + zoneName));
    }

    // ---------------------------------------------------------------- UPDATE

    /** Full replace: fields left out of the body fall back to their defaults. */
    @PutMapping("/{id}")
    public GridZone updateZone(@PathVariable UUID id, @RequestBody GridZone updated) {
        logger.info("Updating (PUT) grid zone with id={}", id);
        GridZone zone = findOrThrow(id);

        normalize(updated);
        validate(updated);

        if (repository.existsByZoneNameIgnoreCaseAndZoneIdNot(updated.getZoneName(), id)) {
            throw new DuplicateResourceException(
                    "A zone named '" + updated.getZoneName() + "' already exists");
        }

        zone.setZoneName(updated.getZoneName());
        zone.setCurrentSolarIrradiance(updated.getCurrentSolarIrradiance());
        zone.setWeatherCondition(updated.getWeatherCondition());
        zone.setTotalAggregateReserveKwh(updated.getTotalAggregateReserveKwh());
        zone.setGridStatus(updated.getGridStatus());
        zone.setBaseMarketClearingPrice(updated.getBaseMarketClearingPrice());

        GridZone savedZone = repository.save(zone);
        logger.info("Grid zone updated (PUT) successfully: id={}", id);
        return savedZone;
    }

    /**
     * Partial update: only the fields present (non-null) in the body are changed.
     * Handy for a simulator / pricing engine pushing live context, e.g.
     * {"currentSolarIrradiance": 640.50, "weatherCondition": "CLOUDY"}
     */
    @PatchMapping("/{id}")
    public GridZone patchZone(@PathVariable UUID id, @RequestBody GridZone patch) {
        logger.info("Patching grid zone with id={}", id);
        GridZone zone = findOrThrow(id);

        if (patch.getZoneName() != null) {
            String name = patch.getZoneName().trim();
            if (name.isEmpty()) {
                throw new BadRequestException("zoneName must not be blank");
            }
            if (repository.existsByZoneNameIgnoreCaseAndZoneIdNot(name, id)) {
                throw new DuplicateResourceException(
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
        GridZone savedZone = repository.save(zone);
        logger.info("Grid zone patched successfully: id={}", id);
        return savedZone;
    }

    // ---------------------------------------------------------------- DELETE

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteZone(@PathVariable UUID id) {
        logger.info("Deleting grid zone with id={}", id);
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Zone", id);
        }
        repository.deleteById(id);
        logger.info("Grid zone deleted successfully: id={}", id);
    }

    // --------------------------------------------------------------- HELPERS

    private GridZone findOrThrow(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Zone", id));
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
            throw new BadRequestException("zoneName is required");
        }
        if (zone.getZoneName().length() > 100) {
            throw new BadRequestException("zoneName must be at most 100 characters");
        }
        requireNonNegative(zone.getCurrentSolarIrradiance(), "currentSolarIrradiance");
        requireNonNegative(zone.getTotalAggregateReserveKwh(), "totalAggregateReserveKwh");
        requireNonNegative(zone.getBaseMarketClearingPrice(), "baseMarketClearingPrice");
    }

    private void requireNonNegative(BigDecimal value, String field) {
        if (value != null && value.signum() < 0) {
            throw new BadRequestException(field + " must not be negative");
        }
    }
}
