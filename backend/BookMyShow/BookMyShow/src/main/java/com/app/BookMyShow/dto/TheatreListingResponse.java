package com.app.BookMyShow.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class TheatreListingResponse {

    private UUID theatreId;

    private String theatreName;

    private List<ShowtimeDto> showtimes;

}
