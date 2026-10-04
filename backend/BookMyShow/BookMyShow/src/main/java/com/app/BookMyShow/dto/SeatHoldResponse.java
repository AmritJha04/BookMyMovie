package com.app.BookMyShow.dto;

import com.app.BookMyShow.enums.HoldStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SeatHoldResponse {

    private UUID holdGroupId ;

    private Instant expiresAt ;

    private List<HeldSeatDto> seats ;

}

