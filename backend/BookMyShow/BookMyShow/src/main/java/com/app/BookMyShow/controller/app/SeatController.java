package com.app.BookMyShow.controller.app;


import com.app.BookMyShow.dto.SeatHoldRequest;
import com.app.BookMyShow.dto.SeatHoldResponse;
import com.app.BookMyShow.service.SeatHoldService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.UUID;

@RestController
@RequestMapping("/shows/{showId}/seat-holds")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RequiredArgsConstructor
public class SeatController {

    private final SeatHoldService seatHoldService;

    @PostMapping(consumes = "application/json")
    public ResponseEntity<SeatHoldResponse> createHold(
            @PathVariable UUID showId,
            @RequestBody SeatHoldRequest request
    ) {
        System.out.println(request.getShowSeatIds());
        SeatHoldResponse response = seatHoldService.createHold(showId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{showSeatId}")
    public ResponseEntity<Void> releaseHold(
            @PathVariable UUID showId,
            @PathVariable UUID showSeatId,
            @RequestParam String sessionId
    ) throws AccessDeniedException {
        try {
            seatHoldService.releaseHold(showId, showSeatId, sessionId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}
