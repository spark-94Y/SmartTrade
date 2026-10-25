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

import com.TradeP.SmartTrade.entity.EnergyOrder;
import com.TradeP.SmartTrade.entity.SmartMeter;
import com.TradeP.SmartTrade.entity.User;
import com.TradeP.SmartTrade.exception.BadRequestException;
import com.TradeP.SmartTrade.exception.ResourceNotFoundException;
import com.TradeP.SmartTrade.repository.EnergyOrderRepository;
import com.TradeP.SmartTrade.repository.SmartMeterRepository;
import com.TradeP.SmartTrade.repository.UserRepository;

@RestController
public class EnergyOrderController {
	private static final Logger logger = LoggerFactory.getLogger(EnergyOrderController.class);

	@Autowired
	private EnergyOrderRepository repo;

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private SmartMeterRepository meterRepo;

	@GetMapping("/order")
	public List<EnergyOrder> order() {
		logger.info("Fetching all energy orders");
		List<EnergyOrder> orders = repo.findAll();
		logger.info("Found {} energy orders", orders.size());
		return orders;
	}

	@GetMapping("/order/{orderId}")
	public ResponseEntity<?> getOrderById(@PathVariable UUID orderId) {
		logger.info("Fetching order with id={}", orderId);
		EnergyOrder order = repo.findById(orderId)
				.orElseThrow(() -> new ResourceNotFoundException("Order", orderId));
		logger.info("Found order: id={}", orderId);
		return ResponseEntity.ok(order);
	}

	@GetMapping("/user/{userId}/orders")
	public ResponseEntity<?> getOrdersByUser(@PathVariable UUID userId) {
		logger.info("Fetching orders for userId={}", userId);
		userRepo.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User", userId));
		List<EnergyOrder> orders = repo.findByUser_UserId(userId);
		logger.info("Found {} orders for userId={}", orders.size(), userId);
		return ResponseEntity.ok(orders);
	}

	@PostMapping("/createorder")
	public ResponseEntity<?> createOrder(@RequestBody EnergyOrder order) {
		logger.info("Creating new energy order");
		if (order.getUser() == null || order.getUser().getUserId() == null) {
			throw new BadRequestException("userId is Required");
		}
		if (order.getMeter() == null || order.getMeter().getMeterId() == null) {
			throw new BadRequestException("meterId is Required");
		}
		if (order.getOrderType() == null) {
			throw new BadRequestException("orderType is Required");
		}
		if (order.getEnergyQuantityKwh() == null || order.getEnergyQuantityKwh().compareTo(BigDecimal.ZERO) <= 0) {
			throw new BadRequestException("energyQuantityKwh Must Be Greater Than Zero");
		}
		if (order.getPricePerKwh() == null || order.getPricePerKwh().compareTo(BigDecimal.ZERO) <= 0) {
			throw new BadRequestException("pricePerKwh Must Be Greater Than Zero");
		}

		User user = userRepo.findById(order.getUser().getUserId())
				.orElseThrow(() -> new ResourceNotFoundException("User", order.getUser().getUserId()));
		SmartMeter meter = meterRepo.findById(order.getMeter().getMeterId())
				.orElseThrow(() -> new ResourceNotFoundException("Meter", order.getMeter().getMeterId()));

		EnergyOrder newOrder = new EnergyOrder();
		newOrder.setUser(user);
		newOrder.setMeter(meter);
		newOrder.setOrderType(order.getOrderType());
		newOrder.setEnergyQuantityKwh(order.getEnergyQuantityKwh());
		newOrder.setRemainingQuantityKwh(order.getEnergyQuantityKwh());
		newOrder.setPricePerKwh(order.getPricePerKwh());
		newOrder.setStatus("OPEN");
		newOrder.setExpiresAt(order.getExpiresAt());

		EnergyOrder savedOrder = repo.save(newOrder);
		logger.info("Energy order created successfully with id={}", savedOrder.getOrderId());
		return ResponseEntity.status(HttpStatus.CREATED).body(savedOrder);
	}

	@PatchMapping("/order/{orderId}")
	public ResponseEntity<?> updateOrder(@PathVariable UUID orderId, @RequestBody EnergyOrder incomingData) {
		logger.info("Updating order with id={}", orderId);
		EnergyOrder existingOrder = repo.findById(orderId)
				.orElseThrow(() -> new ResourceNotFoundException("Order", orderId));

		if (incomingData.getPricePerKwh() != null) {
			existingOrder.setPricePerKwh(incomingData.getPricePerKwh());
		}
		if (incomingData.getStatus() != null) {
			existingOrder.setStatus(incomingData.getStatus());
		}
		if (incomingData.getExpiresAt() != null) {
			existingOrder.setExpiresAt(incomingData.getExpiresAt());
		}

		EnergyOrder updatedOrder = repo.save(existingOrder);
		logger.info("Order updated successfully: id={}", orderId);
		return ResponseEntity.ok(updatedOrder);
	}

	@PatchMapping("/order/{orderId}/cancel")
	public ResponseEntity<?> cancelOrder(@PathVariable UUID orderId) {
		logger.info("Cancelling order with id={}", orderId);
		EnergyOrder order = repo.findById(orderId)
				.orElseThrow(() -> new ResourceNotFoundException("Order", orderId));
		order.setStatus("CANCELLED");
		EnergyOrder cancelledOrder = repo.save(order);
		logger.info("Order cancelled successfully: id={}", orderId);
		return ResponseEntity.ok(cancelledOrder);
	}

	@DeleteMapping("/order/{orderId}")
	public ResponseEntity<?> deleteOrder(@PathVariable UUID orderId) {
		logger.info("Deleting order with id={}", orderId);
		EnergyOrder order = repo.findById(orderId)
				.orElseThrow(() -> new ResourceNotFoundException("Order", orderId));
		repo.delete(order);
		logger.info("Order deleted successfully: id={}", orderId);
		return ResponseEntity.ok("Order Deleted Succesfully");
	}

}
