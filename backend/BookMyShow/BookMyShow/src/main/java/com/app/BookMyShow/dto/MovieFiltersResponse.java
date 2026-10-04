package com.app.BookMyShow.dto;

import com.app.BookMyShow.enums.Genre;
import com.app.BookMyShow.enums.MovieCertificate;
import lombok.*;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class MovieFiltersResponse {
    private List<String> languages;
    private List<Genre> genres;
    private List<String> formats;
  //  private List<TheatreFilter> theatres;

//    @AllArgsConstructor
//    @NoArgsConstructor
//    @Getter
//    @Setter
//    @Builder
//    public static class TheatreFilter {
//        private UUID theatreId;
//        private String theatreName;
//    }
}
