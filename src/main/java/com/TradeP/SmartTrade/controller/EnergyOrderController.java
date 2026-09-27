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

import com.TradeP.SmartTrade.entity.EnergyOrder;
import com.TradeP.SmartTrade.entity.SmartMeter;
import com.TradeP.SmartTrade.entity.User;
import com.TradeP.SmartTrade.repository.EnergyOrderRepository;
import com.TradeP.SmartTrade.repository.SmartMeterRepository;
import com.TradeP.SmartTrade.repository.UserRepository;

@RestController
public class EnergyOrderController {
	@Autowired
	private EnergyOrderRepository repo;

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private SmartMeterRepository meterRepo;

	@GetMapping("/order")
	public List<EnergyOrder> order() {
		return repo.findAll();
	}

	@GetMapping("/order/{orderId}")
	public ResponseEntity<?> getOrderById(@PathVariable UUID orderId) {
		Optional<EnergyOrder> optionalOrder = repo.findById(orderId);
		if (optionalOrder.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Order Not Found");
		}
		return ResponseEntity.ok(optionalOrder.get());
	}

	@GetMapping("/user/{userId}/orders")
	public ResponseEntity<?> getOrdersByUser(@PathVariable UUID userId) {
		Optional<User> optionalUser = userRepo.findById(userId);
		if (optionalUser.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User Not Found");
		}
		return ResponseEntity.ok(repo.findByUser_UserId(userId));
	}

	@PostMapping("/createorder")
	public ResponseEntity<?> createOrder(@RequestBody EnergyOrder order) {
		if (order.getUser() == null || order.getUser().getUserId() == null) {
			return ResponseEntity.badRequest().body("userId is Required");
		}
		if (order.getMeter() == null || order.getMeter().getMeterId() == null) {
			return ResponseEntity.badRequest().body("meterId is Required");
		}
		if (order.getOrderType() == null) {
			return ResponseEntity.badRequest().body("orderType is Required");
		}
		if (order.getEnergyQuantityKwh() == null || order.getEnergyQuantityKwh().compareTo(BigDecimal.ZERO) <= 0) {
			return ResponseEntity.badRequest().body("energyQuantityKwh Must Be Greater Than Zero");
		}
		if (order.getPricePerKwh() == null || order.getPricePerKwh().compareTo(BigDecimal.ZERO) <= 0) {
			return ResponseEntity.badRequest().body("pricePerKwh Must Be Greater Than Zero");
		}

		Optional<User> optionalUser = userRepo.findById(order.getUser().getUserId());
		if (optionalUser.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User Not Found");
		}
		Optional<SmartMeter> optionalMeter = meterRepo.findById(order.getMeter().getMeterId());
		if (optionalMeter.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Meter Not Found");
		}

		EnergyOrder newOrder = new EnergyOrder();
		newOrder.setUser(optionalUser.get());
		newOrder.setMeter(optionalMeter.get());
		newOrder.setOrderType(order.getOrderType());
		newOrder.setEnergyQuantityKwh(order.getEnergyQuantityKwh());
		newOrder.setRemainingQuantityKwh(order.getEnergyQuantityKwh());
		newOrder.setPricePerKwh(order.getPricePerKwh());
		newOrder.setStatus("OPEN");
		newOrder.setExpiresAt(order.getExpiresAt());

		return ResponseEntity.status(HttpStatus.CREATED).body(repo.save(newOrder));
	}

	@PatchMapping("/order/{orderId}")
	public ResponseEntity<?> updateOrder(@PathVariable UUID orderId, @RequestBody EnergyOrder incomingData) {
		Optional<EnergyOrder> optionalOrder = repo.findById(orderId);
		if (optionalOrder.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Order Not Found");
		}
		EnergyOrder existingOrder = optionalOrder.get();
		if (incomingData.getPricePerKwh() != null) {
			existingOrder.setPricePerKwh(incomingData.getPricePerKwh());
		}
		if (incomingData.getStatus() != null) {
			existingOrder.setStatus(incomingData.getStatus());
		}
		if (incomingData.getExpiresAt() != null) {
			existingOrder.setExpiresAt(incomingData.getExpiresAt());
		}
		return ResponseEntity.ok(repo.save(existingOrder));
	}

	@PatchMapping("/order/{orderId}/cancel")
	public ResponseEntity<?> cancelOrder(@PathVariable UUID orderId) {
		Optional<EnergyOrder> optionalOrder = repo.findById(orderId);
		if (optionalOrder.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Order Not Found");
		}
		EnergyOrder order = optionalOrder.get();
		order.setStatus("CANCELLED");
		return ResponseEntity.ok(repo.save(order));
	}

	@DeleteMapping("/order/{orderId}")
	public ResponseEntity<?> deleteOrder(@PathVariable UUID orderId) {
		Optional<EnergyOrder> optionalOrder = repo.findById(orderId);
		if (optionalOrder.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Order Not Found");
		}
		repo.delete(optionalOrder.get());
		return ResponseEntity.ok("Order Deleted Succesfully");
	}

}
