package com.app.BookMyShow.dto;

import lombok.*;

import java.time.LocalTime;
import java.util.UUID;

@Getter
@Builder
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ShowtimeDto {

    private UUID showId ;

    private LocalTime showTime ;

    private String screenName ;

    private String format ;

}
