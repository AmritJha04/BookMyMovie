package com.app.BookMyShow.search.repository;

import com.app.BookMyShow.enums.MovieStatus;
import com.app.BookMyShow.search.document.Movie_Document;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MovieDocumentRepository extends ElasticsearchRepository<Movie_Document, UUID> {

    List<Movie_Document> findByStatusAndMovieIdNotIn(MovieStatus movieStatus, List<UUID> alreadyIncluded);

    List<Movie_Document> findByStatus(MovieStatus movieStatus);
}
