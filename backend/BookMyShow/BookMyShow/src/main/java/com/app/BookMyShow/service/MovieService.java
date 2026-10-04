
        package com.app.BookMyShow.service;

        import co.elastic.clients.elasticsearch._types.aggregations.Aggregate;
        import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
        import co.elastic.clients.elasticsearch._types.aggregations.StringTermsAggregate;
        import com.app.BookMyShow.dto.*;
        import com.app.BookMyShow.enums.Genre;
        import com.app.BookMyShow.enums.MovieStatus;
        import com.app.BookMyShow.enums.ShowStatus;
        import com.app.BookMyShow.repository.MovieRepository;
        import com.app.BookMyShow.search.document.Movie_City_Document;
        import com.app.BookMyShow.search.document.Movie_Document;
        import com.app.BookMyShow.search.document.Show_Document;
        import com.app.BookMyShow.search.repository.MovieCityDocumentRepository;
        import com.app.BookMyShow.search.repository.MovieDocumentRepository;
        import com.app.BookMyShow.search.repository.ShowDocumentRepository;
        import jakarta.persistence.EntityNotFoundException;
        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.data.domain.PageRequest;
        import org.springframework.data.domain.Pageable;
        import org.springframework.data.domain.Sort;
        import org.springframework.data.elasticsearch.client.elc.ElasticsearchAggregation;
        import org.springframework.data.elasticsearch.client.elc.ElasticsearchAggregations;
        import org.springframework.data.elasticsearch.client.elc.NativeQuery;
        import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
        import org.springframework.data.elasticsearch.core.SearchHits;
        import org.springframework.data.elasticsearch.core.query.Criteria;
        import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
        import org.springframework.stereotype.Service;

        import java.time.Instant;
        import java.time.LocalDate;
        import java.time.LocalTime;
        import java.time.ZoneId;
        import java.time.temporal.ChronoUnit;
        import java.util.Comparator;
        import java.util.List;
        import java.util.UUID;
        import java.util.stream.Collectors;
        import java.util.stream.Stream;

@Service
public class MovieService {

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

    @Autowired
    private MovieDocumentRepository movieDocumentRepository;

    @Autowired
    private MovieCityDocumentRepository movieCityDocumentRepository;

    @Autowired
    private ShowDocumentRepository showDocumentRepository;


    public MovieListingResponse getMovies(
            UUID cityId,
            MovieStatus status,
            String q,
            List<String> language,
            List<Genre> genre,
            List<String> format,
            UUID theatreId,
            int page,
            int size,
            String sort) {

        // Build the base criteria against movie_city_listing
        Criteria criteria = new Criteria("cityId").is(cityId.toString());

        if (status != null) {
            criteria = criteria.and(new Criteria("cityStatus").is(status.name()));
        }
        if (q != null && !q.isBlank()) {
            criteria = criteria.and(new Criteria("title").matches(q));
        }
        if (language != null && !language.isEmpty()) {
            criteria = criteria.and(new Criteria("language").in(language));
        }
        if (genre != null && !genre.isEmpty()) {
            criteria = criteria.and(new Criteria("genre").in(genre));
        }
        if (format != null && !format.isEmpty()) {
            criteria = criteria.and(new Criteria("availableFormats").in(format));
        }
        if (theatreId != null) {
            criteria = criteria.and(new Criteria("theatreIds").is(theatreId.toString()));
        }

        Sort esSort = resolveSort(sort, q);

        /*
         * COMING_SOON merge logic
         *
         * Movies that exist in Movie_Document as COMING_SOON but do not
         * have a Movie_City_Document entry for this city are treated as
         * announcements.
         *
         * IMPORTANT: for COMING_SOON, we do NOT let Elasticsearch paginate
         * the city-specific query directly. The announcements batch has to
         * be merged in and sorted BEFORE pagination is applied, otherwise
         * each page ends up larger than `size` (ES already gave us `size`
         * city-specific results, then we tack announcements on top of that
         * — violating the page-size contract). So for this branch we pull
         * the full city-specific Coming Soon list unpaginated, merge, sort,
         * then slice the combined list ourselves.
         */
        if (status == MovieStatus.COMING_SOON) {

            // Fetch ALL city-specific COMING_SOON results, unpaginated —
            // safe at this dataset's scale, and necessary for correct merging.
            CriteriaQuery unpaginatedQuery = new CriteriaQuery(criteria);
            unpaginatedQuery.addSort(esSort);

            SearchHits<Movie_City_Document> cityHits =
                    elasticsearchOperations.search(unpaginatedQuery, Movie_City_Document.class);

            List<MovieSummaryDto> combined = cityHits.stream()
                    .map(hit -> toSummaryDto(hit.getContent()))
                    .collect(Collectors.toList());

            List<UUID> cityMovieIds = movieCityDocumentRepository.findByCityId(cityId).stream()
                    .map(Movie_City_Document::getMovieId)
                    .toList();

            boolean announcementsCanMatch = theatreId == null && (format == null || format.isEmpty());

            if (announcementsCanMatch) {
                List<Movie_Document> announcementsOnly = cityMovieIds.isEmpty()
                        ? movieDocumentRepository.findByStatus(MovieStatus.COMING_SOON)
                        : movieDocumentRepository.findByStatusAndMovieIdNotIn(MovieStatus.COMING_SOON, cityMovieIds);

                Stream<Movie_Document> filtered = announcementsOnly.stream();

                if (language != null && !language.isEmpty()) {
                    filtered = filtered.filter(doc -> language.contains(doc.getLanguage()));
                }
                if (genre != null && !genre.isEmpty()) {
                    filtered = filtered.filter(doc -> genre.contains(doc.getGenre()));
                }
                if (q != null && !q.isBlank()) {
                    String needle = q.toLowerCase();
                    filtered = filtered.filter(doc -> doc.getTitle() != null
                            && doc.getTitle().toLowerCase().contains(needle));
                }

                List<MovieSummaryDto> announcementDtos = filtered
                        .map(this::toSummaryDtoFromMovieDocument)
                        .toList();

                combined.addAll(announcementDtos);
            }

            // Sort the FULL combined list before slicing — sorting only the
            // page-0 subset would leave later pages out of order.
            combined.sort(Comparator.comparing(MovieSummaryDto::getReleaseDate));

            long totalResults = combined.size();

            // Manual pagination over the combined, sorted list.
            int fromIndex = Math.min(page * size, combined.size());
            int toIndex = Math.min(fromIndex + size, combined.size());
            List<MovieSummaryDto> pageResults = combined.subList(fromIndex, toIndex);

            return MovieListingResponse.builder()
                    .movies(pageResults)
                    .page(page)
                    .size(size)
                    .totalResults(totalResults)
                    .build();
        }

        // NOW_SHOWING (and any other status) — unchanged, ES handles pagination directly.
        Pageable pageable = PageRequest.of(page, size, esSort);
        CriteriaQuery query = new CriteriaQuery(criteria, pageable);

        SearchHits<Movie_City_Document> hits =
                elasticsearchOperations.search(query, Movie_City_Document.class);

        List<MovieSummaryDto> results = hits.stream()
                .map(hit -> toSummaryDto(hit.getContent()))
                .collect(Collectors.toList());

        return MovieListingResponse.builder()
                .movies(results)
                .page(page)
                .size(size)
                .totalResults(hits.getTotalHits())
                .build();
    }


    private Sort resolveSort(String sort, String q) {

        return switch (sort) {

            case "title,asc" -> Sort.by(
                    Sort.Direction.ASC,
                    "title.raw"
            );

            case "releaseDate,desc" -> Sort.by(
                    Sort.Direction.DESC,
                    "releasedAt"
            );

            default -> (q != null && !q.isBlank())
                    ? Sort.unsorted()
                    : Sort.by(
                    Sort.Direction.DESC,
                    "releasedAt"
            );
        };
    }


    private MovieSummaryDto toSummaryDto(
            Movie_City_Document doc) {

        return MovieSummaryDto.builder()
                .movieId(doc.getMovieId())
                .title(doc.getTitle())
                .posterUrl(doc.getPosterUrl())
                .language(doc.getLanguage())
                .genre(doc.getGenre())
                .cityStatus(doc.getCityStatus())
                .availableFormats(doc.getAvailableFormats())
                .releaseDate(doc.getReleasedAt())
                .certificate(
                        doc.getCertificate().toString()
                )
                .rating(doc.getRating())
                .earliestShowTime(
                        doc.getEarliestShowTime()
                )
                .build();
    }


    private MovieSummaryDto toSummaryDtoFromMovieDocument(
            Movie_Document doc) {

        return MovieSummaryDto.builder()
                .movieId(doc.getMovieId())
                .title(doc.getTitle())
                .posterUrl(doc.getPosterUrl())
                .language(doc.getLanguage())
                .genre(doc.getGenre())
                .cityStatus(MovieStatus.COMING_SOON)
                .availableFormats(List.of())
                .releaseDate(doc.getReleasedAt())
                .build();
    }


    public MovieDetailsResponse getMovieDetails(
            UUID movieId,
            UUID cityId) {

        Movie_Document movie =
                movieDocumentRepository.findById(movieId)
                        .orElseThrow(
                                () -> new EntityNotFoundException(
                                        "Movie not found with ID: "
                                                + movieId
                                )
                        );
        System.out.println(movie.getDescription());
  //      System.out.println(2);

        Movie_City_Document cityContext  =
                movieCityDocumentRepository
                        .findByMovieIdAndCityId(
                                movieId,
                                cityId
                        );
    //    System.out.println(cityContext);
    //    System.out.println(1);
        // cityContext can legitimately be null if the movie
        // has no shows in this city yet.

        MovieDetailsResponse.MovieDetailsResponseBuilder builder = MovieDetailsResponse.builder()
                .movieId(movie.getMovieId())
                .title(movie.getTitle())
                .description(movie.getDescription())
                .language(movie.getLanguage())
                .genre(movie.getGenre())
                // .duration(movie.getDuration())
                .releaseDate(movie.getReleasedAt())
                .posterUrl(movie.getPosterUrl())
                .certificate(movie.getCertificate())
                .rating(movie.getRating())
                .availableDates(getAvailableDates(movieId, cityId))
                .cityStatus(
                        cityContext != null
                                ? cityContext.getCityStatus()
                                : null
                )
                .showCount(
                        cityContext != null
                                ? cityContext.getShowCount()
                                : 0
                )
                .theatreCount(
                        cityContext != null
                                ? cityContext.getTheatreIds() != null
                                ? cityContext.getTheatreIds().size()
                                : 0
                                : 0
                );

        // 2. Conditionals can safely be appended right onto the builder instance
        if (cityContext != null) {
            builder.availableFormats(cityContext.getAvailableFormats());
        }

        return builder.build();


    }




    public MovieFiltersResponse getMovieFilters(
            UUID cityId) {

        NativeQuery query = NativeQuery.builder()

                .withQuery(
                        q -> q.term(
                                t -> t.field("cityId")
                                        .value(cityId.toString())
                        )
                )

                .withAggregation(
                        "languages",
                        Aggregation.of(
                                a -> a.terms(
                                        t -> t.field("language")
                                )
                        )
                )

                .withAggregation(
                        "genres",
                        Aggregation.of(
                                a -> a.terms(
                                        t -> t.field("genre")
                                )
                        )
                )

                .withAggregation(
                        "formats",
                        Aggregation.of(
                                a -> a.terms(
                                        t -> t.field("availableFormats")
                                )
                        )
                )

                .withMaxResults(0)
                .build();


        SearchHits<Movie_City_Document> result =
                elasticsearchOperations.search(
                        query,
                        Movie_City_Document.class
                );

        ElasticsearchAggregations aggregations =
                (ElasticsearchAggregations) result.getAggregations();


        return MovieFiltersResponse.builder()

                .languages(
                        extractBucketKeys(
                                aggregations,
                                "languages"
                        )
                )

                .genres(
                        extractBucketKeys(
                                aggregations,
                                "genres"
                        )
                                .stream()
                                .map(Genre::valueOf)
                                .toList()
                )

                .formats(
                        extractBucketKeys(
                                aggregations,
                                "formats"
                        )
                )

                .build();
    }


    private List<String> extractBucketKeys(
            ElasticsearchAggregations aggregations,
            String aggregationName) {

        if (aggregations == null) {
            return List.of();
        }

        ElasticsearchAggregation aggregation =
                aggregations.get(aggregationName);

        if (aggregation == null) {
            return List.of();
        }

        Aggregate aggregate =
                aggregation
                        .aggregation()
                        .getAggregate();

        StringTermsAggregate termsAggregate =
                aggregate.sterms();

        if (termsAggregate == null) {
            return List.of();
        }

        return termsAggregate
                .buckets()
                .array()
                .stream()
                .map(bucket -> bucket.key().stringValue())
                .toList();
    }


    public List<TheatreListingResponse> getTheatresForMovie(
            UUID movieId,
            UUID cityId,
            LocalDate date,
            String format) {

        List<Show_Document> shows = (format == null || format.isBlank())
                ? showDocumentRepository.findByMovieIdAndCityIdAndShowDate(movieId, cityId, date)
                : showDocumentRepository.findByMovieIdAndCityIdAndShowDateAndFormat(movieId, cityId, date, format);

        return shows.stream()
                .collect(Collectors.groupingBy(Show_Document::getTheatreId))
                .entrySet()
                .stream()
                .map(entry -> {
                    List<Show_Document> theatreShows = entry.getValue();
                    Show_Document first = theatreShows.get(0);

                    List<ShowtimeDto> showtimes = theatreShows.stream()
                            .map(s -> new ShowtimeDto(
                                    s.getShowId(),
                                    LocalTime.ofInstant(s.getStartTime(), ZoneId.of("Asia/Kolkata")),
                                    s.getScreenName(),
                                    s.getFormat()
                            ))
                            .sorted(Comparator.comparing(ShowtimeDto::getShowTime))
                            .toList();

                    return TheatreListingResponse.builder()
                            .theatreId(first.getTheatreId())
                            .theatreName(first.getTheatreName())
                            .showtimes(showtimes)
                            .build();
                })
                .sorted(Comparator.comparing(TheatreListingResponse::getTheatreName))
                .toList();
    }

    private List<LocalDate> getAvailableDates(UUID movieId, UUID cityId) {

        Instant now = Instant.now();
        Instant windowEnd = now.plus(14, ChronoUnit.DAYS);

        List<Show_Document> shows =
                showDocumentRepository
                        .findByMovieIdAndCityIdAndShowStatusAndStartTimeBetween(
                                movieId,
                                cityId,
                                ShowStatus.SCHEDULED,
                                now,
                                windowEnd
                        );

        return shows.stream()
                .map(Show_Document::getShowDate)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }
}

