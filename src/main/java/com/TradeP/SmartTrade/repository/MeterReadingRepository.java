package com.TradeP.SmartTrade.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.TradeP.SmartTrade.entity.MeterReading;

@Repository
public interface MeterReadingRepository extends JpaRepository<MeterReading, UUID> {

    /** All readings of one meter, newest first. */
    List<MeterReading> findByMeter_MeterIdOrderByTimestampDesc(UUID meterId);

    /** The most recent reading of one meter. */
    Optional<MeterReading> findFirstByMeter_MeterIdOrderByTimestampDesc(UUID meterId);

    /** Readings of one meter inside a time window, oldest first (handy for charts). */
    List<MeterReading> findByMeter_MeterIdAndTimestampBetweenOrderByTimestampAsc(
            UUID meterId, LocalDateTime from, LocalDateTime to);

    boolean existsByMeter_MeterId(UUID meterId);
}
