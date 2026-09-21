package com.TradeP.SmartTrade.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

/**
 * MeterReading Entity
 * -------------------
 * reading_id (PK), meter_id (FK), timestamp, current_generation_kwh,
 * current_consumption_kwh, net_grid_flow_kwh, battery_soc_percentage
 *
 * Readings are append-only: once stored they are never updated.
 */
@Entity
@Table(name = "meter_reading")
public class MeterReading {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "reading_id", nullable = false, updatable = false)
    private UUID readingId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "meter_id", nullable = false, updatable = false)
    private SmartMeter meter;

    @Column(name = "timestamp", nullable = false, updatable = false)
    private LocalDateTime timestamp;

    @Column(name = "current_generation_kwh", nullable = false, updatable = false, precision = 12, scale = 4)
    private BigDecimal currentGenerationKwh = BigDecimal.ZERO;

    @Column(name = "current_consumption_kwh", nullable = false, updatable = false, precision = 12, scale = 4)
    private BigDecimal currentConsumptionKwh = BigDecimal.ZERO;

    /** Positive = surplus exported to the grid, negative = imported from the grid. */
    @Column(name = "net_grid_flow_kwh", nullable = false, updatable = false, precision = 12, scale = 4)
    private BigDecimal netGridFlowKwh = BigDecimal.ZERO;

    /** Battery state of charge, 0 to 100. Null when the meter has no battery. */
    @Column(name = "battery_soc_percentage", updatable = false, precision = 5, scale = 2)
    private BigDecimal batterySocPercentage;

    public MeterReading() {
    }

    @PrePersist
    void onCreate() {
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
    }

    public UUID getReadingId() {
        return readingId;
    }

    public void setReadingId(UUID readingId) {
        this.readingId = readingId;
    }

    public SmartMeter getMeter() {
        return meter;
    }

    public void setMeter(SmartMeter meter) {
        this.meter = meter;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public BigDecimal getCurrentGenerationKwh() {
        return currentGenerationKwh;
    }

    public void setCurrentGenerationKwh(BigDecimal currentGenerationKwh) {
        this.currentGenerationKwh = currentGenerationKwh;
    }

    public BigDecimal getCurrentConsumptionKwh() {
        return currentConsumptionKwh;
    }

    public void setCurrentConsumptionKwh(BigDecimal currentConsumptionKwh) {
        this.currentConsumptionKwh = currentConsumptionKwh;
    }

    public BigDecimal getNetGridFlowKwh() {
        return netGridFlowKwh;
    }

    public void setNetGridFlowKwh(BigDecimal netGridFlowKwh) {
        this.netGridFlowKwh = netGridFlowKwh;
    }

    public BigDecimal getBatterySocPercentage() {
        return batterySocPercentage;
    }

    public void setBatterySocPercentage(BigDecimal batterySocPercentage) {
        this.batterySocPercentage = batterySocPercentage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MeterReading that = (MeterReading) o;
        return readingId != null && Objects.equals(readingId, that.readingId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(readingId);
    }
}
