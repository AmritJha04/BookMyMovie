package com.app.BookMyShow.service;

import com.app.BookMyShow.Exceptions.SeatUnavailableException;
import com.app.BookMyShow.Exceptions.ShowNotBookableException;
import com.app.BookMyShow.dto.HeldSeatDto;
import com.app.BookMyShow.dto.SeatHoldRequest;
import com.app.BookMyShow.dto.SeatHoldResponse;
import com.app.BookMyShow.entity.SeatHold;
import com.app.BookMyShow.entity.Show;
import com.app.BookMyShow.entity.ShowSeat;
import com.app.BookMyShow.enums.HoldStatus;
import com.app.BookMyShow.enums.ShowSeatStatus;
import com.app.BookMyShow.repository.SeatHoldRepository;
import com.app.BookMyShow.repository.ShowRepository;
import com.app.BookMyShow.repository.ShowSeatRepository;

import jakarta.persistence.PersistenceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.elasticsearch.ResourceNotFoundException;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class SeatHoldService {

    @Autowired
    private ShowRepository showRepository;
    @Autowired
    private  ShowSeatRepository showSeatRepository;
    @Autowired
    private SeatHoldRepository seatHoldRepository;

    @Value("${seat-hold.duration-seconds:300}")
    private long holdDurationSeconds;

    @Transactional
    public SeatHoldResponse createHold(UUID showId, SeatHoldRequest request) {

        if (request.getShowSeatIds() == null || request.getShowSeatIds().isEmpty()) {
            throw new IllegalArgumentException("showSeatIds must not be empty");
        }

        Show show = showRepository.findById(showId)
                .orElseThrow(() -> new ResourceNotFoundException("Show not found: " + showId));

        if (show.getStartTime().isBefore(Instant.now())) {
            throw new ShowNotBookableException("Show has already started");
        }
        List<ShowSeat> lockedSeats = showSeatRepository.lockForHold(showId, request.getShowSeatIds());
        if (lockedSeats.size() != request.getShowSeatIds().size()) {
            throw new ResourceNotFoundException(
                    "One or more showSeatIds do not belong to this show");
        }

        List<ShowSeat> unavailable = lockedSeats.stream()
                .filter(ss -> ss.getShowSeatStatus() != ShowSeatStatus.AVAILABLE)
                .toList();

        if (!unavailable.isEmpty()) {
            List<UUID> ids = unavailable.stream().map(ShowSeat::getShowSeatId).toList();
            throw new SeatUnavailableException("Seats no longer available: " + ids);
        }

        UUID holdGroupId = UUID.randomUUID();
        Instant now = Instant.now();
        Instant expiresAt = now.plusSeconds(holdDurationSeconds);
        System.out.println("execution 1 ");
        try {
            for (ShowSeat seat : lockedSeats) {
                SeatHold hold = new SeatHold();
                hold.setHoldGroupId(holdGroupId);
                hold.setShowSeat(seat);
                hold.setSessionId(request.getSessionId());
                hold.setHoldStatus(HoldStatus.ACTIVE);
                hold.setCreatedAt(now);
                hold.setExpiresAt(expiresAt);
                seatHoldRepository.save(hold);
            }
        } catch (DataIntegrityViolationException | PersistenceException e) {
            // Backstop: the partial unique index rejected a race the lock
            // somehow didn't prevent. Surface as a clean 409, not a 500.
            throw new SeatUnavailableException(
                    "One or more seats were held by another request just now");
        }
        System.out.println("execution 2 ");
        List<UUID> seatIds = lockedSeats.stream().map(ShowSeat::getShowSeatId).toList();
        showSeatRepository.updateStatusForSeats(seatIds, ShowSeatStatus.HELD);

        List<HeldSeatDto> heldSeatDtos = seatIds.stream()
                .map(id -> new HeldSeatDto(id, ShowSeatStatus.HELD))
                .toList();

        return new SeatHoldResponse(holdGroupId, expiresAt, heldSeatDtos);
    }





    @Transactional
    public void releaseHold(UUID showId, UUID showSeatId, String sessionId) throws AccessDeniedException {
        SeatHold hold = seatHoldRepository
                .findByShowSeat_ShowSeatIdAndHoldStatus(showSeatId, HoldStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("No active hold on this seat"));

        if (!hold.getShowSeat().getShow().getShowId().equals(showId)) {
            throw new ResourceNotFoundException("Hold does not belong to this show");
        }

        if (!hold.getSessionId().equals(sessionId)) {
            throw new AccessDeniedException(
                    "Cannot release another session's hold");
        }

        hold.setHoldStatus(HoldStatus.RELEASED);
        seatHoldRepository.save(hold);

        showSeatRepository.updateStatusForSeats(
                List.of(showSeatId), ShowSeatStatus.AVAILABLE);
    }
}
