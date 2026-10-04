package com.app.BookMyShow.repository;

import com.app.BookMyShow.entity.Show;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface ShowRepository extends JpaRepository<Show, UUID> {

    @Query("""
        SELECT s FROM Show s
        JOIN FETCH s.movie m
        JOIN FETCH s.screen sc
        JOIN FETCH sc.theatre t
        JOIN FETCH t.city c
        WHERE s.showStatus <> com.app.BookMyShow.enums.ShowStatus.CANCELLED
        """)
    List<Show> findAllActiveShowsWithDetails();

    @Query("""
        SELECT COUNT(s) > 0 FROM Show s
        WHERE s.screen.screenId = :screenId
          AND s.showStatus <> 'CANCELLED'
          AND s.startTime < :endTime
          AND s.endTime > :startTime
    """)
    boolean existsOverlappingShow(
            @Param("screenId") UUID screenId,
            @Param("startTime") Instant startTime,
            @Param("endTime") Instant endTime
    );
}