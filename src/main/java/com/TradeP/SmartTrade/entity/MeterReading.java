package com.TradeP.SmartTrade.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "meter_reading")
public class MeterReading {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "reading_id", updatable = false, nullable = false)
    private UUID readingId;

    @Column(name = "meter_id", nullable = false)
    private UUID meterId;

    @Column(name = "reading_value", nullable = false, precision = 12, scale = 4)
    private BigDecimal readingValue;

    @Column(name = "reading_type", nullable = false, length = 20)
    private String readingType; // 'GENERATION' or 'CONSUMPTION'

    @Column(name = "timestamp", nullable = false, updatable = false)
    private LocalDateTime timestamp = LocalDateTime.now();

    // Constructors
    public MeterReading() {}

    public MeterReading(UUID meterId, BigDecimal readingValue, String readingType) {
        this.meterId = meterId;
        this.readingValue = readingValue;
        this.readingType = readingType;
        this.timestamp = LocalDateTime.now();
    }

    public MeterReading(UUID readingId, UUID meterId, BigDecimal readingValue, String readingType, LocalDateTime timestamp) {
        this.readingId = readingId;
        this.meterId = meterId;
        this.readingValue = readingValue;
        this.readingType = readingType;
        this.timestamp = timestamp != null ? timestamp : LocalDateTime.now();
    }

    // Getters and Setters
    public UUID getReadingId() {
        return readingId;
    }

    public void setReadingId(UUID readingId) {
        this.readingId = readingId;
    }

    public UUID getMeterId() {
        return meterId;
    }

    public void setMeterId(UUID meterId) {
        this.meterId = meterId;
    }

    public BigDecimal getReadingValue() {
        return readingValue;
    }

    public void setReadingValue(BigDecimal readingValue) {
        this.readingValue = readingValue;
    }

    public String getReadingType() {
        return readingType;
    }

    public void setReadingType(String readingType) {
        this.readingType = readingType;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MeterReading that = (MeterReading) o;
        return Objects.equals(readingId, that.readingId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(readingId);
    }

    @Override
    public String toString() {
        return "MeterReading{" +
                "readingId=" + readingId +
                ", meterId=" + meterId +
                ", readingValue=" + readingValue +
                ", readingType='" + readingType + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}

