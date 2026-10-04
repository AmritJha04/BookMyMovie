package com.app.BookMyShow.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ShowSeatsListResponse {

    private UUID showId;

    private UUID screenId;

    private String screenName;

    private String theatreName ;

    private String posterUrl ;

    private String format ;

    private String movieTitle ;

    private LocalTime showTime ;

    private List<ShowSeatResponseDto> seats;

}
