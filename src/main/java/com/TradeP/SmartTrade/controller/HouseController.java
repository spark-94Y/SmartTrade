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

import com.TradeP.SmartTrade.entity.House;
import com.TradeP.SmartTrade.exception.BadRequestException;
import com.TradeP.SmartTrade.exception.ResourceNotFoundException;
import com.TradeP.SmartTrade.repository.HouseRepository;

@RestController
public class HouseController {
    private static final Logger logger = LoggerFactory.getLogger(HouseController.class);

    @Autowired
    private HouseRepository repo;

    @GetMapping("/house")
    public List<House> getAllHouses() {
        logger.info("Fetching all houses");
        List<House> houses = repo.findAll();
        logger.info("Found {} houses", houses.size());
        return houses;
    }

    @GetMapping("/house/{houseId}")
    public ResponseEntity<?> getHouseById(@PathVariable UUID houseId) {
        logger.info("Fetching house with id={}", houseId);
        House house = repo.findById(houseId)
                .orElseThrow(() -> new ResourceNotFoundException("House", houseId));
        logger.info("Found house: id={}", houseId);
        return ResponseEntity.ok(house);
    }

    @GetMapping("/house/user/{userId}")
    public ResponseEntity<?> getHousesByUserId(@PathVariable UUID userId) {
        logger.info("Fetching houses for userId={}", userId);
        List<House> houses = repo.findByUser_UserId(userId);
        logger.info("Found {} houses for userId={}", houses.size(), userId);
        return ResponseEntity.ok(houses);
    }

    @PostMapping("/createhouse")
    public ResponseEntity<?> createHouse(@RequestBody House house) {
        logger.info("Creating new house");
        if (house.getUserId() == null || house.getZoneId() == null) {
            throw new BadRequestException("userId and zoneId are required");
        }

        House newHouse = new House();
        newHouse.setUserId(house.getUserId());
        newHouse.setZoneId(house.getZoneId());
        newHouse.setAddress(house.getAddress());
        newHouse.setSolarCapacityKw(house.getSolarCapacityKw());
        newHouse.setConnectionType(house.getConnectionType());

        House savedHouse = repo.save(newHouse);
        logger.info("House created successfully with id={}", savedHouse.getHouseId());
        return ResponseEntity.status(HttpStatus.CREATED).body(savedHouse);
    }

    @PatchMapping("/house/{houseId}")
    public ResponseEntity<?> updateHouse(@PathVariable UUID houseId, @RequestBody House incomingData) {
        logger.info("Updating house with id={}", houseId);
        House existingHouse = repo.findById(houseId)
                .orElseThrow(() -> new ResourceNotFoundException("House", houseId));

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

        House updatedHouse = repo.save(existingHouse);
        logger.info("House updated successfully: id={}", houseId);
        return ResponseEntity.ok(updatedHouse);
    }

    @DeleteMapping("/house/{houseId}")
    public ResponseEntity<?> deleteHouse(@PathVariable UUID houseId) {
        logger.info("Deleting house with id={}", houseId);
        House house = repo.findById(houseId)
                .orElseThrow(() -> new ResourceNotFoundException("House", houseId));

        repo.delete(house);
        logger.info("House deleted successfully: id={}", houseId);
        return ResponseEntity.ok("House Deleted Successfully");
    }
}