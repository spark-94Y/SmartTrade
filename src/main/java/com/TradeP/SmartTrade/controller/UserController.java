package com.TradeP.SmartTrade.controller;

import java.math.BigDecimal;
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

import com.TradeP.SmartTrade.entity.User;
import com.TradeP.SmartTrade.exception.BadRequestException;
import com.TradeP.SmartTrade.exception.ResourceNotFoundException;
import com.TradeP.SmartTrade.repository.UserRepository;

@RestController
public class UserController {
	private static final Logger logger = LoggerFactory.getLogger(UserController.class);

	@Autowired
	private UserRepository repo;

	@GetMapping("/user")
	public List<User> user() {
		logger.info("Fetching all users");
		List<User> users = repo.findAll();
		logger.info("Found {} users", users.size());
		return users;
	}

	@GetMapping("/user/{userId}")
	public ResponseEntity<?> getUserById(@PathVariable UUID userId) {
		logger.info("Fetching user with id={}", userId);
		User user = repo.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User", userId));
		logger.info("Found user: {}", user.getEmail());
		return ResponseEntity.ok(user);
	}

	@GetMapping("/user/{userId}/wallet")
	public ResponseEntity<?> getUserWalletBalance(@PathVariable UUID userId) {
		logger.info("Fetching wallet balance for userId={}", userId);
		User user = repo.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User", userId));
		BigDecimal balance = user.getWalletBalance();
		logger.info("Wallet balance for userId={}: {}", userId, balance);
		return ResponseEntity.ok(balance);
	}

	@PostMapping("/createuser")
	public ResponseEntity<?> createUser(@RequestBody User user) {
		logger.info("Creating new user with email={}", user.getEmail());
		if (user.getEmail() == null || user.getPasswordHash() == null) {
			throw new BadRequestException("Email and Password is Required");
		}
		User newUser = new User();
		newUser.setEmail(user.getEmail());
		newUser.setFullName(user.getFullName());
		newUser.setRole(user.getRole() != null ? user.getRole() : "USER");
		newUser.setPasswordHash(user.getPasswordHash());

		User savedUser = repo.save(newUser);
		logger.info("User created successfully with id={}", savedUser.getUserId());
		return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
	}

	@PostMapping("/user/{userId}/wallet/deposit")
	public ResponseEntity<?> updateWallet(@PathVariable UUID userId, @RequestBody User depositAmount) {
		logger.info("Depositing to wallet for userId={}", userId);
		BigDecimal amount = depositAmount.getWalletBalance();
		if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
			throw new BadRequestException("Deposited Amount Must Be Greater Than Zero.");
		}
		User user = repo.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User", userId));

		BigDecimal account = user.getWalletBalance();
		BigDecimal updatedAmount = account.add(amount);
		user.setWalletBalance(updatedAmount);

		repo.save(user);
		logger.info("Wallet updated for userId={}, newBalance={}", userId, updatedAmount);
		return ResponseEntity.ok(user);
	}

	@PatchMapping("/user/{userId}")
	public ResponseEntity<?> updateUser(@PathVariable UUID userId, @RequestBody User incomingData) {
		logger.info("Updating user with id={}", userId);
		User existingUser = repo.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User", userId));

		if (incomingData.getFullName() != null) {
			existingUser.setFullName(incomingData.getFullName());
		}
		if (incomingData.getRole() != null) {
			existingUser.setRole(incomingData.getRole());
		}
		if (incomingData.getPasswordHash() != null) {
			existingUser.setPasswordHash(incomingData.getPasswordHash());
		}

		User updatedUser = repo.save(existingUser);
		logger.info("User updated successfully: id={}", userId);
		return ResponseEntity.ok(updatedUser);
	}

	@DeleteMapping("/user/{userId}")
	public ResponseEntity<?> deleteUser(@PathVariable UUID userId) {
		logger.info("Deleting user with id={}", userId);
		User user = repo.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User", userId));

		repo.delete(user);
		logger.info("User deleted successfully: id={}", userId);
		return ResponseEntity.ok("User Deleted Succesfully");
	}

}
