package com.TradeP.SmartTrade.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "\"user\"")
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "user_id", updatable = false, nullable = false)
	private UUID userId;

	@Column(unique = true, nullable = false)
	private String email;

	@Column(name = "full_name")
	private String fullName;

	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@Column(name = "password_hash")
	private String passwordHash;

	@Column(name = "wallet_balance", precision = 12, scale = 4, nullable = false)
	private BigDecimal walletBalance = new BigDecimal("0.0000");

	private String role;

	@CreationTimestamp
	@Column(name = "created_at", updatable = false, nullable = false)
	private LocalDateTime createdAt;

	@JsonIgnore
	@OneToMany(mappedBy = "user")
	private List<House> houses;

	@JsonIgnore
	@OneToMany(mappedBy = "user")
	private List<EnergyOrder> orders;

	@JsonIgnore
	@OneToMany(mappedBy = "buyer")
	private List<EnergyTrade> buyerTrades;

	@JsonIgnore
	@OneToMany(mappedBy = "seller")
	private List<EnergyTrade> sellerTrades;

	@JsonIgnore
	@OneToMany(mappedBy = "user")
	private List<WalletTransaction> walletTransactions;

	public void setUserId(UUID userId) {
		this.userId = userId;
	}

	public UUID getUserId() {
		return userId;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEmail() {
		return email;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getFullName() {
		return fullName;
	}

	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public BigDecimal getWalletBalance() {
		return walletBalance;
	}

	public void setWalletBalance(BigDecimal walletBalance) {
		this.walletBalance = walletBalance;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public List<House> getHouses() {
		return houses;
	}

	public void setHouses(List<House> houses) {
		this.houses = houses;
	}

	public List<EnergyOrder> getOrders() {
		return orders;
	}

	public void setOrders(List<EnergyOrder> orders) {
		this.orders = orders;
	}

	public List<EnergyTrade> getBuyerTrades() {
		return buyerTrades;
	}

	public void setBuyerTrades(List<EnergyTrade> buyerTrades) {
		this.buyerTrades = buyerTrades;
	}

	public List<EnergyTrade> getSellerTrades() {
		return sellerTrades;
	}

	public void setSellerTrades(List<EnergyTrade> sellerTrades) {
		this.sellerTrades = sellerTrades;
	}

	public List<WalletTransaction> getWalletTransactions() {
		return walletTransactions;
	}

	public void setWalletTransactions(List<WalletTransaction> walletTransactions) {
		this.walletTransactions = walletTransactions;
	}
}
