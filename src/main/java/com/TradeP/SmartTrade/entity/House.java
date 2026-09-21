package com.TradeP.SmartTrade.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "house")
public class House {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "house_id", updatable = false, nullable = false)
    private UUID houseId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "zone_id", nullable = false)
    private Long zoneId;

    @Column(name = "address", columnDefinition = "TEXT")
    private String address;

    @Column(name = "solar_capacity_kw", precision = 6, scale = 2)
    private BigDecimal solarCapacityKw;

    @Column(name = "connection_type", length = 30)
    private String connectionType;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    public House() {
    }

    public UUID getHouseId() {
        return houseId;
    }

    public void setHouseId(UUID houseId) {
        this.houseId = houseId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public Long getZoneId() {
        return zoneId;
    }

    public void setZoneId(Long zoneId) {
        this.zoneId = zoneId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public BigDecimal getSolarCapacityKw() {
        return solarCapacityKw;
    }

    public void setSolarCapacityKw(BigDecimal solarCapacityKw) {
        this.solarCapacityKw = solarCapacityKw;
    }

    public String getConnectionType() {
        return connectionType;
    }

    public void setConnectionType(String connectionType) {
        this.connectionType = connectionType;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}