package com.app.BookMyShow.dto;

import com.app.BookMyShow.dto.MovieSummaryDto;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovieListingResponse {
    private List<MovieSummaryDto> movies;
    private int page;
    private int size;
    private long totalResults;
}