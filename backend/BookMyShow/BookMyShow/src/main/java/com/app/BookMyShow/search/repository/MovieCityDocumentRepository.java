package com.app.BookMyShow.search.repository;

import com.app.BookMyShow.search.document.Movie_City_Document;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Repository
public interface MovieCityDocumentRepository
        extends ElasticsearchRepository<Movie_City_Document,String> {

    Movie_City_Document findByMovieIdAndCityId(UUID movieId, UUID cityId);

    List<Movie_City_Document> findByCityId(UUID cityId);
}

