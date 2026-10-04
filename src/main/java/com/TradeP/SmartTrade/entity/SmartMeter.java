package com.TradeP.SmartTrade.entity;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

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
@Table(name = "smart_meter")
public class SmartMeter {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "meter_id", nullable = false, updatable = false)
    private UUID meterId;

    @ManyToOne
    @JoinColumn(name = "house_id", nullable = false)
    private House house;

    @Column(name = "meter_number", nullable = false, unique = true, length = 100)
    private String meterNumber;

    @Column(name = "hardware_version", length = 50)
    private String hardwareVersion;

    @Column(name = "firmware_version", length = 50)
    private String firmwareVersion;

    @Column(name = "installation_date")
    private LocalDate installationDate;

    @Column(name = "status", length = 30)
    private String status = "ONLINE";

    @JsonIgnore
    @OneToMany(mappedBy = "meter")
    private List<MeterReading> meterReadings;

    @JsonIgnore
    @OneToMany(mappedBy = "meter")
    private List<EnergyOrder> energyOrders;

    public SmartMeter() {
    }

    public UUID getMeterId() {
        return meterId;
    }

    public void setMeterId(UUID meterId) {
        this.meterId = meterId;
    }

    public House getHouse() {
        return house;
    }

    public void setHouse(House house) {
        this.house = house;
    }

    public UUID getHouseId() {
        return house != null ? house.getHouseId() : null;
    }

    public void setHouseId(UUID houseId) {
        if (houseId != null) {
            House h = new House();
            h.setHouseId(houseId);
            this.house = h;
        } else {
            this.house = null;
        }
    }

    public String getMeterNumber() {
        return meterNumber;
    }

    public void setMeterNumber(String meterNumber) {
        this.meterNumber = meterNumber;
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

    public LocalDate getInstallationDate() {
        return installationDate;
    }

    public void setInstallationDate(LocalDate installationDate) {
        this.installationDate = installationDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<MeterReading> getMeterReadings() {
        return meterReadings;
    }

    public void setMeterReadings(List<MeterReading> meterReadings) {
        this.meterReadings = meterReadings;
    }

    public List<EnergyOrder> getEnergyOrders() {
        return energyOrders;
    }

    public void setEnergyOrders(List<EnergyOrder> energyOrders) {
        this.energyOrders = energyOrders;
    }
}
