package com.TradeP.SmartTrade.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "house")
public class House {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "house_id", updatable = false, nullable = false)
    private UUID houseId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "zone_id")
    private GridZone gridZone;

    @Column(name = "address", columnDefinition = "TEXT")
    private String address;

    @Column(name = "solar_capacity_kw", precision = 6, scale = 2)
    private BigDecimal solarCapacityKw;

    @Column(name = "connection_type", length = 30)
    private String connectionType;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @JsonIgnore
    @OneToMany(mappedBy = "house")
    private List<SmartMeter> smartMeters;

    public House() {
    }

    public UUID getHouseId() {
        return houseId;
    }

    public void setHouseId(UUID houseId) {
        this.houseId = houseId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public UUID getUserId() {
        return user != null ? user.getUserId() : null;
    }

    public void setUserId(UUID userId) {
        if (userId != null) {
            User u = new User();
            u.setUserId(userId);
            this.user = u;
        } else {
            this.user = null;
        }
    }

    public GridZone getGridZone() {
        return gridZone;
    }

    public void setGridZone(GridZone gridZone) {
        this.gridZone = gridZone;
    }

    public UUID getZoneId() {
        return gridZone != null ? gridZone.getZoneId() : null;
    }

    public void setZoneId(UUID zoneId) {
        if (zoneId != null) {
            GridZone gz = new GridZone();
            gz.setZoneId(zoneId);
            this.gridZone = gz;
        } else {
            this.gridZone = null;
        }
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

    public List<SmartMeter> getSmartMeters() {
        return smartMeters;
    }

    public void setSmartMeters(List<SmartMeter> smartMeters) {
        this.smartMeters = smartMeters;
    }
}