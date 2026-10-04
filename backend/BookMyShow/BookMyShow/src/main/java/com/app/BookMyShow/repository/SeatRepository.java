package com.app.BookMyShow.repository;

import com.app.BookMyShow.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SeatRepository extends JpaRepository <Seat, UUID> {
    List<Seat> findAllByScreen_ScreenId(UUID screenId);
}
