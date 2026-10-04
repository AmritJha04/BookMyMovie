package com.app.BookMyShow.dto;

import com.app.BookMyShow.enums.Genre;
import com.app.BookMyShow.enums.MovieCertificate;
import com.app.BookMyShow.enums.MovieStatus;
import lombok.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovieSummaryDto {
    private UUID movieId;
    private String title;
    private String posterUrl;
    private String language;
    private Genre genre;
    private String certificate;
    private Float rating;               // Float, not double — see Bug 4
    private MovieStatus cityStatus;
    private List<String> availableFormats;
    private Instant earliestShowTime;
    private Instant releaseDate;
}
