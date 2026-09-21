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

import com.TradeP.SmartTrade.entity.House;
import com.TradeP.SmartTrade.repository.HouseRepository;

@RestController
public class HouseController {

    @Autowired
    private HouseRepository repo;

    @GetMapping("/house")
    public List<House> getAllHouses() {
        return repo.findAll();
    }

    @GetMapping("/house/{houseId}")
    public ResponseEntity<?> getHouseById(@PathVariable UUID houseId) {
        Optional<House> optionalHouse = repo.findById(houseId);
        if (optionalHouse.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("House Not Found");
        }
        return ResponseEntity.ok(optionalHouse.get());
    }

    @GetMapping("/house/user/{userId}")
    public ResponseEntity<?> getHousesByUserId(@PathVariable UUID userId) {
        List<House> houses = repo.findByUserId(userId);
        return ResponseEntity.ok(houses);
    }

    @PostMapping("/createhouse")
    public ResponseEntity<?> createHouse(@RequestBody House house) {
        if (house.getUserId() == null || house.getZoneId() == null) {
            return ResponseEntity.badRequest().body("userId and zoneId are required");
        }

        House newHouse = new House();
        newHouse.setUserId(house.getUserId());
        newHouse.setZoneId(house.getZoneId());
        newHouse.setAddress(house.getAddress());
        newHouse.setSolarCapacityKw(house.getSolarCapacityKw());
        newHouse.setConnectionType(house.getConnectionType());

        return ResponseEntity.status(HttpStatus.CREATED).body(repo.save(newHouse));
    }

    @PatchMapping("/house/{houseId}")
    public ResponseEntity<?> updateHouse(@PathVariable UUID houseId, @RequestBody House incomingData) {
        Optional<House> optionalHouse = repo.findById(houseId);
        if (optionalHouse.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("House Not Found");
        }

        House existingHouse = optionalHouse.get();

        if (incomingData.getAddress() != null) {
            existingHouse.setAddress(incomingData.getAddress());
        }
        if (incomingData.getSolarCapacityKw() != null) {
            existingHouse.setSolarCapacityKw(incomingData.getSolarCapacityKw());
        }
        if (incomingData.getConnectionType() != null) {
            existingHouse.setConnectionType(incomingData.getConnectionType());
        }
        if (incomingData.getZoneId() != null) {
            existingHouse.setZoneId(incomingData.getZoneId());
        }

        return ResponseEntity.ok(repo.save(existingHouse));
    }

    @DeleteMapping("/house/{houseId}")
    public ResponseEntity<?> deleteHouse(@PathVariable UUID houseId) {
        Optional<House> optionalHouse = repo.findById(houseId);
        if (optionalHouse.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("House Not Found");
        }

        repo.delete(optionalHouse.get());
        return ResponseEntity.ok("House Deleted Successfully");
    }
}