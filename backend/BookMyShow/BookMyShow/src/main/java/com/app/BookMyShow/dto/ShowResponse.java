package com.app.BookMyShow.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ShowResponse {

    private UUID showId ;

    private UUID movieId ;

    private UUID screenId ;

    private Instant startTime ;

    private Instant endTime ;

    private String showStatus;

    private BigDecimal classicPrice;

    private BigDecimal premiumPrice;

    private BigDecimal reclinerPrice;

    private int seatsGenerated ;

}
