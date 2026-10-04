package com.app.BookMyShow.dto;

import com.app.BookMyShow.enums.Genre;
import com.app.BookMyShow.enums.MovieCertificate;
import com.app.BookMyShow.enums.MovieStatus;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MovieDetailsResponse {

    private UUID movieId;

    private String title;

    private String description;

    private String language;

    private Genre genre;

   // private Integer duration;

    private Instant releaseDate;

    private String posterUrl;

    private MovieCertificate certificate;

    private Float rating;

    private int showCount ;

    private MovieStatus cityStatus ;

    private int theatreCount;

    private List<String> availableFormats;

    private List<LocalDate> availableDates;

}
