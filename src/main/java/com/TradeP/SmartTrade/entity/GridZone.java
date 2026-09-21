package com.TradeP.SmartTrade.entity;

// merged with remote main - keeping complete implementation
import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * GridZone Context
 * ----------------
 * A geographic/electrical zone of the grid. It holds the live "context"
 * (weather, solar irradiance, aggregate reserve, grid health and the base
 * clearing price) that the trading engine uses when matching orders for
 * every smart meter that belongs to this zone.
 *
 * Columns follow the ER table:
 * zone_id (PK), zone_name, current_solar_irradiance, weather_condition,
 * total_aggregate_reserve_kwh, grid_status, base_market_clearing_price
 */
@Entity
@Table(name = "grid_zone")
public class GridZone {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "zone_id", nullable = false, updatable = false)
    private UUID zoneId;

    @Column(name = "zone_name", nullable = false, unique = true, length = 100)
    private String zoneName;

    /** Solar irradiance in W/m2 (0 at night, roughly 1000 in full sun). */
    @Column(name = "current_solar_irradiance", nullable = false, precision = 8, scale = 2)
    private BigDecimal currentSolarIrradiance = BigDecimal.ZERO;

    /** e.g. CLEAR, CLOUDY, RAIN, STORM, UNKNOWN. */
    @Column(name = "weather_condition", length = 50)
    private String weatherCondition = "UNKNOWN";

    /** Sum of stored energy (batteries) available in this zone, in kWh. */
    @Column(name = "total_aggregate_reserve_kwh", nullable = false, precision = 14, scale = 4)
    private BigDecimal totalAggregateReserveKwh = BigDecimal.ZERO;

    /** e.g. STABLE, CONGESTED, OVERLOADED, OFFLINE. */
    @Column(name = "grid_status", nullable = false, length = 30)
    private String gridStatus = "STABLE";

    /** Base price per kWh before order-book matching. Same scale as prices elsewhere (12,4). */
    @Column(name = "base_market_clearing_price", nullable = false, precision = 12, scale = 4)
    private BigDecimal baseMarketClearingPrice = BigDecimal.ZERO;

    public GridZone() {
    }

    public GridZone(String zoneName) {
        this.zoneName = zoneName;
    }

    public UUID getZoneId() {
        return zoneId;
    }

    public void setZoneId(UUID zoneId) {
        this.zoneId = zoneId;
    }

    public String getZoneName() {
        return zoneName;
    }

    public void setZoneName(String zoneName) {
        this.zoneName = zoneName;
    }

    public BigDecimal getCurrentSolarIrradiance() {
        return currentSolarIrradiance;
    }

    public void setCurrentSolarIrradiance(BigDecimal currentSolarIrradiance) {
        this.currentSolarIrradiance = currentSolarIrradiance;
    }

    public String getWeatherCondition() {
        return weatherCondition;
    }

    public void setWeatherCondition(String weatherCondition) {
        this.weatherCondition = weatherCondition;
    }

    public BigDecimal getTotalAggregateReserveKwh() {
        return totalAggregateReserveKwh;
    }

    public void setTotalAggregateReserveKwh(BigDecimal totalAggregateReserveKwh) {
        this.totalAggregateReserveKwh = totalAggregateReserveKwh;
    }

    public String getGridStatus() {
        return gridStatus;
    }

    public void setGridStatus(String gridStatus) {
        this.gridStatus = gridStatus;
    }

    public BigDecimal getBaseMarketClearingPrice() {
        return baseMarketClearingPrice;
    }

    public void setBaseMarketClearingPrice(BigDecimal baseMarketClearingPrice) {
        this.baseMarketClearingPrice = baseMarketClearingPrice;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GridZone that = (GridZone) o;
        return zoneId != null && Objects.equals(zoneId, that.zoneId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(zoneId);
    }

    @Override
    public String toString() {
        return "GridZone{" +
                "zoneId=" + zoneId +
                ", zoneName='" + zoneName + '\'' +
                ", currentSolarIrradiance=" + currentSolarIrradiance +
                ", weatherCondition='" + weatherCondition + '\'' +
                ", totalAggregateReserveKwh=" + totalAggregateReserveKwh +
                ", gridStatus='" + gridStatus + '\'' +
                ", baseMarketClearingPrice=" + baseMarketClearingPrice +
                '}';
    }
}
