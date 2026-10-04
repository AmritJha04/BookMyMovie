package com.app.BookMyShow.controller.app;

import com.app.BookMyShow.dto.*;
import com.app.BookMyShow.enums.Genre;
import com.app.BookMyShow.enums.MovieStatus;
import com.app.BookMyShow.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List ;
import java.util.UUID;

@RestController
@RequestMapping("/movies")
@CrossOrigin(origins = "http://localhost:5173",allowCredentials = "true")
public class MovieController {

      @Autowired
      private MovieService movieService ;


      @GetMapping
      public ResponseEntity <MovieListingResponse>getMovies(
              @RequestParam UUID cityId,
              @RequestParam(required = false) MovieStatus status,
              @RequestParam(required = false) String q,
              @RequestParam(required = false) List<String> language,
              @RequestParam(required = false) List<Genre> genre,
              @RequestParam(required = false) List<String> format,
              @RequestParam(required = false) UUID theatreId,
              @RequestParam(defaultValue = "0") int page,
              @RequestParam(defaultValue = "5") int size,
              @RequestParam(defaultValue = "relevance") String sort) {

            try {
                 int cappedSize = Math.min(size,20);
                 MovieListingResponse response = movieService.getMovies(cityId
                         , status, q , language , genre , format , theatreId , page , cappedSize , sort );
                 // System.out.println("method is working");
                 return ResponseEntity.status(HttpStatus.OK).body(response);
            }catch (Exception e){
                  //e.printStackTrace();
                        Throwable t = e;
                        while (t != null) {
                              System.out.println(t.getClass().getName() + ": " + t.getMessage());
                              t = t.getCause();
                        }

                  return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
      }

      @GetMapping("/filters")
      public ResponseEntity<MovieFiltersResponse> getMovieFilters(
              @RequestParam UUID cityId) {
            try {
                    MovieFiltersResponse response = movieService.getMovieFilters(cityId);
                    return ResponseEntity.status(HttpStatus.OK).body(response);
            }catch (Exception e){
                  System.out.println(e.getMessage());
                  return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }

      }

      @GetMapping("/{movieId}")
      public ResponseEntity<MovieDetailsResponse> getMovieDetails(
              @PathVariable UUID movieId,
              @RequestParam UUID cityId) {
            // Validate inputs
            try {
                    MovieDetailsResponse response = movieService.getMovieDetails(movieId, cityId);
                    return ResponseEntity.status(HttpStatus.OK).body(response);
            }catch (Exception e){
                System.out.println("Exception occured");
                  return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
            // Delegate to service layer

      }

      @GetMapping("/{movieId}/theatres")
      public ResponseEntity<List<TheatreListingResponse>> getTheatresForMovie(
              @PathVariable UUID movieId,
              @RequestParam UUID cityId,
              @RequestParam(required = false) LocalDate date,
              @RequestParam(required = false) String format) {
            // Validate inputs
            try {
                  List<TheatreListingResponse> response = movieService.getTheatresForMovie(movieId, cityId, date, format);
                  return ResponseEntity.status(HttpStatus.OK).body(response);
            }catch (Exception e){
                  System.out.println(e.getMessage());
                  return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
            // Delegate to service layer

      }


}
