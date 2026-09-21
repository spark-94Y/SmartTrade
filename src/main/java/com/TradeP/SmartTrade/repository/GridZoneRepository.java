package com.TradeP.SmartTrade.repository;

// merged with remote main - keeping complete implementation
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.TradeP.SmartTrade.entity.GridZone;

@Repository
public interface GridZoneRepository extends JpaRepository<GridZone, UUID> {

    Optional<GridZone> findByZoneNameIgnoreCase(String zoneName);

    // Used to reject duplicate zone names on create
    boolean existsByZoneNameIgnoreCase(String zoneName);

    // Used to reject duplicate zone names on update (ignores the zone being updated)
    boolean existsByZoneNameIgnoreCaseAndZoneIdNot(String zoneName, UUID zoneId);

    List<GridZone> findByGridStatusIgnoreCase(String gridStatus);

    List<GridZone> findByWeatherConditionIgnoreCase(String weatherCondition);
}
