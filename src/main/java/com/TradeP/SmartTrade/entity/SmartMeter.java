package com.TradeP.SmartTrade.entity;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * SmartMeter Entity
 * -----------------
 * meter_id (PK), user_id (FK), zone_id (FK), hardware_version,
 * firmware_version, latitude, longitude, status
 */
@Entity
@Table(name = "smart_meter")
public class SmartMeter {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "meter_id", nullable = false, updatable = false)
    private UUID meterId;

    /** Owner of the meter. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /** Grid zone the meter is connected to. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "zone_id", nullable = false)
    private GridZone zone;

    @Column(name = "hardware_version", length = 50)
    private String hardwareVersion;

    @Column(name = "firmware_version", length = 50)
    private String firmwareVersion;

    /** Decimal degrees, -90 to 90. */
    @Column(name = "latitude", precision = 9, scale = 6)
    private BigDecimal latitude;

    /** Decimal degrees, -180 to 180. */
    @Column(name = "longitude", precision = 9, scale = 6)
    private BigDecimal longitude;

    /** e.g. ONLINE, OFFLINE, MAINTENANCE. */
    @Column(name = "status", nullable = false, length = 30)
    private String status = "ONLINE";

    public SmartMeter() {
    }

    public UUID getMeterId() {
        return meterId;
    }

    public void setMeterId(UUID meterId) {
        this.meterId = meterId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public GridZone getZone() {
        return zone;
    }

    public void setZone(GridZone zone) {
        this.zone = zone;
    }

    public String getHardwareVersion() {
        return hardwareVersion;
    }

    public void setHardwareVersion(String hardwareVersion) {
        this.hardwareVersion = hardwareVersion;
    }

    public String getFirmwareVersion() {
        return firmwareVersion;
    }

    public void setFirmwareVersion(String firmwareVersion) {
        this.firmwareVersion = firmwareVersion;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SmartMeter that = (SmartMeter) o;
        return meterId != null && Objects.equals(meterId, that.meterId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(meterId);
    }
}
