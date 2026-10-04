package com.app.BookMyShow.search.repository;


import com.app.BookMyShow.enums.ShowStatus;
import com.app.BookMyShow.search.document.Show_Document;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository

public interface ShowDocumentRepository
        extends ElasticsearchRepository<Show_Document, UUID> {


    List<Show_Document> findByMovieIdAndCityIdAndShowStatusAndStartTimeBetween(UUID movieId, UUID cityId, ShowStatus showStatus, Instant now, Instant windowEnd);

    List<Show_Document> findByMovieIdAndCityIdAndShowDateAndFormat(UUID movieId, UUID cityId, LocalDate date, String format);

    List<Show_Document> findByMovieIdAndCityIdAndShowDate(UUID movieId, UUID cityId, LocalDate date);
}