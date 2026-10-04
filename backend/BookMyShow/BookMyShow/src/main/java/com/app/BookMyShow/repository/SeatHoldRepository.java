package com.app.BookMyShow.repository;

import com.app.BookMyShow.entity.SeatHold;
import com.app.BookMyShow.enums.HoldStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface SeatHoldRepository extends JpaRepository<SeatHold, UUID> {

    Optional<SeatHold> findByShowSeat_ShowSeatIdAndHoldStatus(UUID showSeatId, HoldStatus holdStatus);

    @Modifying
    @Query("""
        UPDATE SeatHold sh SET sh.holdStatus = 'EXPIRED'
        WHERE sh.holdStatus = 'ACTIVE' AND sh.expiresAt < :now
    """)
    int markExpiredHolds(@Param("now") Instant now);

}
