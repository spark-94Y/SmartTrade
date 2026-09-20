package com.TradeP.SmartTrade.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.TradeP.SmartTrade.entity.User;
import com.TradeP.SmartTrade.repository.UserRepository;
@RestController 
public class UserController {
    @Autowired
    private UserRepository repo ;

    @GetMapping("/user")
    public List<User> user(){
       return repo.findAll();
        
    }
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserById(@PathVariable UUID userId) {
    	Optional<User> optionalUser = repo.findById(userId);
    	if(optionalUser.isEmpty()) {
    		return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User Not Found");
    	}
    	User user = optionalUser.get();
    	return ResponseEntity.ok(user);
    }
    @GetMapping("/user/{userId}/wallet")
    public ResponseEntity<?> getUserWalletBalance(@PathVariable UUID userId) {
    	Optional<User> optionalUser = repo.findById(userId);
    	if(optionalUser.isEmpty()) {
    		return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User Not Found");
    	}
    	User user = optionalUser.get();
    	BigDecimal balance = user.getWalletBalance();
    	
    	return ResponseEntity.ok(balance);
    }
}

