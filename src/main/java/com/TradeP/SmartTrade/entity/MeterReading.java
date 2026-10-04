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
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "meter_reading")
public class MeterReading {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "reading_id", updatable = false, nullable = false)
    private UUID readingId;

    @ManyToOne
    @JoinColumn(name = "meter_id", nullable = false)
    private SmartMeter meter;

    @Column(name = "reading_value", nullable = false, precision = 12, scale = 4)
    private BigDecimal readingValue;

    @Column(name = "reading_type", nullable = false, length = 20)
    private String readingType;

    @CreationTimestamp
    @Column(name = "timestamp", nullable = false, updatable = false)
    private LocalDateTime timestamp;

    public MeterReading() {
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

    public UUID getMeterId() {
        return meter != null ? meter.getMeterId() : null;
    }

    public void setMeterId(UUID meterId) {
        if (meterId != null) {
            SmartMeter m = new SmartMeter();
            m.setMeterId(meterId);
            this.meter = m;
        } else {
            this.meter = null;
        }
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
}