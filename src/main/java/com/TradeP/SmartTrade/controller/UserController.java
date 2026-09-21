package com.TradeP.SmartTrade.controller;

import java.math.BigDecimal;
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

import com.TradeP.SmartTrade.entity.User;
import com.TradeP.SmartTrade.repository.UserRepository;

@RestController
public class UserController {
	@Autowired
	private UserRepository repo;

	@GetMapping("/user")
	public List<User> user() {
		return repo.findAll();

	}

	@GetMapping("/user/{userId}")
	public ResponseEntity<?> getUserById(@PathVariable UUID userId) {
		Optional<User> optionalUser = repo.findById(userId);
		if (optionalUser.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User Not Found");
		}
		User user = optionalUser.get();
		return ResponseEntity.ok(user);
	}

	@GetMapping("/user/{userId}/wallet")
	public ResponseEntity<?> getUserWalletBalance(@PathVariable UUID userId) {
		Optional<User> optionalUser = repo.findById(userId);
		if (optionalUser.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User Not Found");
		}
		User user = optionalUser.get();
		BigDecimal balance = user.getWalletBalance();

		return ResponseEntity.ok(balance);
	}

	@PostMapping("/createuser")
	public ResponseEntity<?> createUser(@RequestBody User user) {
		if (user.getEmail() == null || user.getPasswordHash() == null) {
			return ResponseEntity.badRequest().body("Email and Password is Required");
		}
		User newUser = new User();
		newUser.setEmail(user.getEmail());
		newUser.setFullName(user.getFullName());
		newUser.setRole(user.getRole() != null ? user.getRole() : "USER");
		newUser.setPasswordHash(user.getPasswordHash());

		return ResponseEntity.status(HttpStatus.CREATED).body(repo.save(newUser));
	}

	@PostMapping("/user/{userId}/wallet/deposit")
	public ResponseEntity<?> updateWallet(@PathVariable UUID userId, @RequestBody User depositAmount) {
		BigDecimal amount = depositAmount.getWalletBalance();
		if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
			return ResponseEntity.badRequest().body("Deposited Amount Must Be Greater Than Zero.");
		}
		Optional<User> optionalUser = repo.findById(userId);
		if (optionalUser.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User Not Found");

		}

		User user = optionalUser.get();
		BigDecimal account = user.getWalletBalance();
		BigDecimal updatedAmount = account.add(amount);
		user.setWalletBalance(updatedAmount);

		repo.save(user);
		return ResponseEntity.ok(user);

	}

	@PatchMapping("/user/{userId}")
	public ResponseEntity<?> updateUser(@PathVariable UUID userId, @RequestBody User incomingData) {
		Optional<User> optionalUser = repo.findById(userId);
		if (optionalUser.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User Not Found");

		}
		User existingUser = optionalUser.get();
		if (incomingData.getFullName() != null) {
			existingUser.setFullName(incomingData.getFullName());
		}
		if (incomingData.getRole() != null) {
			existingUser.setRole(incomingData.getRole());
		}
		if (incomingData.getPasswordHash() != null) {
			existingUser.setPasswordHash(incomingData.getPasswordHash());
		}

		return ResponseEntity.ok(repo.save(existingUser));

	}

	@DeleteMapping("/user/{userId}")
	public ResponseEntity<?> deleteUser(@PathVariable UUID userId) {
		Optional<User> optionalUser = repo.findById(userId);
		if (optionalUser.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User Not Found");

		}

		repo.delete(optionalUser.get());
		return ResponseEntity.ok("User Deleted Succesfully");
	}

}
