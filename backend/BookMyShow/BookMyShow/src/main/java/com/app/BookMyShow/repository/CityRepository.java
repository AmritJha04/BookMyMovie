package com.app.BookMyShow.repository;

import com.app.BookMyShow.entity.City;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface CityRepository extends JpaRepository<City, UUID> {

    @Query(
            """ 
            SELECT c
            FROM City c 
            WHERE 
              LOWER(c.cityName) LIKE LOWER(CONCAT(COALESCE(:query, ''), '%'))
              ORDER BY c.cityName ASC , c.cityId ASC 
            """
    )
    List<City> findFirstCities(Pageable pageable , @Param("query") String query);


    @Query("""
    SELECT c
    FROM City c
    WHERE 
       LOWER(c.cityName) LIKE LOWER(CONCAT(:query, '%'))
       AND(
          c.cityName > :cityName
       OR (c.cityName = :cityName AND c.cityId > :cityId)
       )
    ORDER BY c.cityName ASC, c.cityId ASC
    """)
        List<City> findNextCities(
                @Param("cityName") String cityName,
                @Param("cityId") UUID cityId,
                @Param("query") String query,
                Pageable pageable);
}
