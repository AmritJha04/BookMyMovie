package com.app.BookMyShow.repository;

import com.app.BookMyShow.entity.ShowSeat;
import com.app.BookMyShow.enums.ShowSeatStatus;
import jakarta.persistence.LockModeType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ShowSeatRepository extends JpaRepository<ShowSeat, UUID> {

    @Query("""
        SELECT ss FROM ShowSeat ss
        JOIN FETCH ss.seat s
        WHERE ss.show.showId = :showId
        ORDER BY s.rowNo, s.seatNo
    """)
    List<ShowSeat> findAllForShow(UUID showId);


    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT ss FROM ShowSeat ss
        WHERE ss.showSeatId IN :showSeatIds AND ss.show.showId = :showId
    """)
    List<ShowSeat> lockForHold(
           @Param("showId") UUID showId,
           @Param("showSeatIds") List<UUID> showSeatIds
    );


    @Modifying
    @Query("""
        UPDATE ShowSeat ss SET ss.showSeatStatus = :showSeatStatus
        WHERE ss.showSeatId IN :seatIds
    """)
    void updateStatusForSeats(
            @Param("seatIds") List<UUID> seatIds,
            @Param("showSeatStatus") ShowSeatStatus showSeatStatus
    );
}
