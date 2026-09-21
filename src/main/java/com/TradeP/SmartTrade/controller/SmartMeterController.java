package com.TradeP.SmartTrade.controller;

import java.math.BigDecimal;
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

import com.TradeP.SmartTrade.dto.SmartMeterRequest;
import com.TradeP.SmartTrade.dto.SmartMeterResponse;
import com.TradeP.SmartTrade.entity.GridZone;
import com.TradeP.SmartTrade.entity.SmartMeter;
import com.TradeP.SmartTrade.entity.User;
import com.TradeP.SmartTrade.repository.EnergyOrderRepository;
import com.TradeP.SmartTrade.repository.GridZoneRepository;
import com.TradeP.SmartTrade.repository.MeterReadingRepository;
import com.TradeP.SmartTrade.repository.SmartMeterRepository;
import com.TradeP.SmartTrade.repository.UserRepository;

@RestController
@RequestMapping("/api/meters")
public class SmartMeterController {

    private static final BigDecimal MAX_LAT = new BigDecimal("90");
    private static final BigDecimal MAX_LON = new BigDecimal("180");

    private final SmartMeterRepository meterRepo;
    private final UserRepository userRepo;
    private final GridZoneRepository zoneRepo;
    private final EnergyOrderRepository orderRepo;
    private final MeterReadingRepository readingRepo;

    public SmartMeterController(SmartMeterRepository meterRepo,
                                UserRepository userRepo,
                                GridZoneRepository zoneRepo,
                                EnergyOrderRepository orderRepo,
                                MeterReadingRepository readingRepo) {
        this.meterRepo = meterRepo;
        this.userRepo = userRepo;
        this.zoneRepo = zoneRepo;
        this.orderRepo = orderRepo;
        this.readingRepo = readingRepo;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SmartMeterResponse createMeter(@RequestBody SmartMeterRequest req) {
        SmartMeter meter = new SmartMeter();
        apply(meter, req);
        return SmartMeterResponse.from(meterRepo.save(meter));
    }

    @GetMapping
    public List<SmartMeterResponse> getAllMeters() {
        return meterRepo.findAll().stream().map(SmartMeterResponse::from).toList();
    }

    @GetMapping("/{id}")
    public SmartMeterResponse getMeter(@PathVariable UUID id) {
        return SmartMeterResponse.from(find(id));
    }

    @GetMapping("/user/{userId}")
    public List<SmartMeterResponse> getMetersByUser(@PathVariable UUID userId) {
        return meterRepo.findByUser_UserId(userId).stream().map(SmartMeterResponse::from).toList();
    }

    @GetMapping("/zone/{zoneId}")
    public List<SmartMeterResponse> getMetersByZone(@PathVariable UUID zoneId) {
        return meterRepo.findByZone_ZoneId(zoneId).stream().map(SmartMeterResponse::from).toList();
    }

    @PutMapping("/{id}")
    public SmartMeterResponse updateMeter(@PathVariable UUID id, @RequestBody SmartMeterRequest req) {
        SmartMeter meter = find(id);
        apply(meter, req);
        return SmartMeterResponse.from(meterRepo.save(meter));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMeter(@PathVariable UUID id) {
        if (!meterRepo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Meter not found");
        }
        if (orderRepo.existsByMeter_MeterId(id) || readingRepo.existsByMeter_MeterId(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Meter still has orders or readings, so it cannot be deleted. Set its status to OFFLINE instead.");
        }
        meterRepo.deleteById(id);
    }

    // ------------------------------------------------------------------

    private SmartMeter find(UUID id) {
        return meterRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Meter not found"));
    }

    /** Validates the request and copies it onto the meter. Used by create and update. */
    private void apply(SmartMeter meter, SmartMeterRequest req) {
        if (req.userId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userId is required");
        }
        if (req.zoneId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "zoneId is required");
        }
        if (req.latitude() != null && req.latitude().abs().compareTo(MAX_LAT) > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "latitude must be between -90 and 90");
        }
        if (req.longitude() != null && req.longitude().abs().compareTo(MAX_LON) > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "longitude must be between -180 and 180");
        }

        User user = userRepo.findById(req.userId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        GridZone zone = zoneRepo.findById(req.zoneId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Grid zone not found"));

        meter.setUser(user);
        meter.setZone(zone);
        meter.setHardwareVersion(req.hardwareVersion());
        meter.setFirmwareVersion(req.firmwareVersion());
        meter.setLatitude(req.latitude());
        meter.setLongitude(req.longitude());
        if (req.status() != null) {
            meter.setStatus(req.status()); // omitted status keeps the current value (ONLINE for a new meter)
        }
    }
}
