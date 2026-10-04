package com.app.BookMyShow.repository;

import com.app.BookMyShow.entity.Movie;
import com.app.BookMyShow.enums.MovieStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MovieRepository extends JpaRepository< Movie,UUID> {

    @Query("""
           SELECT m FROM Movie m 
           WHERE m.status = :status 
           AND m.movieId IN (
               SELECT DISTINCT s.movie.movieId
               FROM Show s 
               JOIN s.screen sc 
               JOIN sc.theatre t 
               JOIN t.city c 
               WHERE c.cityId = :cityId
           )
           """)
    List<Movie> findHomepageMovies(
            @Param("cityId") UUID cityId,
            @Param("status") MovieStatus status,
            Pageable pageable
    );

    List<Movie> findByStatus(MovieStatus status);

}
