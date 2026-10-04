package com.app.BookMyShow.repository;

import com.app.BookMyShow.entity.City;
import com.app.BookMyShow.entity.Theatre;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TheatreRepository extends JpaRepository<Theatre, UUID> {

    @Query("""
        SELECT t
        FROM Theatre t
        WHERE t.city.cityId = :cityId
          AND (
              :q IS NULL
              OR :q = ''
              OR LOWER(t.theatreName) LIKE LOWER(CONCAT('%', :q, '%'))
          )
        ORDER BY t.theatreName ASC
    """)
    List<Theatre> findTheatresByCity(
            @Param("cityId") UUID cityId,
            @Param("q") String q
    );
}
