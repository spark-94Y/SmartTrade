package com.TradeP.SmartTrade.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "energy_order")
public class EnergyOrder {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "order_id", updatable = false, nullable = false)
	private UUID orderId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "meter_id", nullable = false)
	private SmartMeter meter;

	@Column(name = "order_type", length = 10, nullable = false)
	private String orderType;

	@Column(name = "energy_quantity_kwh", precision = 10, scale = 4, nullable = false)
	private BigDecimal energyQuantityKwh;

	@Column(name = "remaining_quantity_kwh", precision = 10, scale = 4, nullable = false)
	private BigDecimal remainingQuantityKwh;

	@Column(name = "price_per_kwh", precision = 10, scale = 4, nullable = false)
	private BigDecimal pricePerKwh;

	@Column(name = "status", length = 30, nullable = false)
	private String status = "OPEN";

	@CreationTimestamp
	@Column(name = "created_at", updatable = false, nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "expires_at")
	private LocalDateTime expiresAt;

	public UUID getOrderId() {
		return orderId;
	}

	public void setOrderId(UUID orderId) {
		this.orderId = orderId;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public SmartMeter getMeter() {
		return meter;
	}

	public void setMeter(SmartMeter meter) {
		this.meter = meter;
	}

	public String getOrderType() {
		return orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

	public BigDecimal getEnergyQuantityKwh() {
		return energyQuantityKwh;
	}

	public void setEnergyQuantityKwh(BigDecimal energyQuantityKwh) {
		this.energyQuantityKwh = energyQuantityKwh;
	}

	public BigDecimal getRemainingQuantityKwh() {
		return remainingQuantityKwh;
	}

	public void setRemainingQuantityKwh(BigDecimal remainingQuantityKwh) {
		this.remainingQuantityKwh = remainingQuantityKwh;
	}

	public BigDecimal getPricePerKwh() {
		return pricePerKwh;
	}

	public void setPricePerKwh(BigDecimal pricePerKwh) {
		this.pricePerKwh = pricePerKwh;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getExpiresAt() {
		return expiresAt;
	}

	public void setExpiresAt(LocalDateTime expiresAt) {
		this.expiresAt = expiresAt;
	}

}