package com.app.BookMyShow.search.service;

import com.app.BookMyShow.entity.City;
import com.app.BookMyShow.entity.Movie;
import com.app.BookMyShow.entity.Show;
import com.app.BookMyShow.enums.MovieStatus;
import com.app.BookMyShow.repository.MovieRepository;
import com.app.BookMyShow.repository.ShowRepository;
import com.app.BookMyShow.search.document.Movie_City_Document;
import com.app.BookMyShow.search.document.Movie_Document;
import com.app.BookMyShow.search.document.Show_Document;
import com.app.BookMyShow.search.repository.MovieCityDocumentRepository;
import com.app.BookMyShow.search.repository.MovieDocumentRepository;
import com.app.BookMyShow.search.repository.ShowDocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
public class ReindexService {

    @Autowired
    private ShowRepository showRepository ;

    @Autowired
    private ShowDocumentRepository showDocumentRepository;

    @Autowired
    private MovieCityDocumentRepository movieCityDocumentRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private MovieDocumentRepository movieDocumentRepository ;

    public List<Show_Document> buildShowDocuments(List<Show> shows) {
        // Logic to transform Show entities into Show_Document format for Elasticsearch indexing

        if (shows.isEmpty()) {
            throw new IllegalArgumentException("Show list cannot be empty");
        }
        List<Show_Document> showDocuments = shows.stream().map(
                s->Show_Document.builder()
                        .showId((s.getShowId()))
                        .movieId((s.getMovie().getMovieId()))
                        .movieTitle(s.getMovie().getTitle())
                        .cityId(s.getScreen().getTheatre().getCity().getCityId())
                        .theatreId(s.getScreen().getTheatre().getTheatreId())
                        .theatreName(s.getScreen().getTheatre().getTheatreName())
                        .format(s.getScreen().getScreenType())
                        .screenId(s.getScreen().getScreenId())
                        .screenName(s.getScreen().getName())
                        .startTime(s.getStartTime())
                        .endTime(s.getEndTime())
                        .showDate(LocalDate.ofInstant(s.getStartTime(), ZoneOffset.UTC))
                        .showStatus(s.getShowStatus())
                        // .price(parsePrice(s.getPrice()))   // if you add the field
                        .build())
                .toList();

        showDocumentRepository.saveAll(showDocuments);

        // This would typically involve mapping the fields from the Show entity to the corresponding fields in the Show_Document.
        return showDocuments; // Placeholder return statement
    }

    private static final Duration NOW_SHOWING_WINDOW = Duration.ofHours(24);

    public List<Movie_City_Document> buildMovieCityDocuments(List<Show> shows) {
        if (shows.isEmpty()) {
            throw new IllegalArgumentException("Show list cannot be empty");
        }

        record MovieCityKey(UUID movieId, UUID cityId) {}

        Map<MovieCityKey, List<Show>> grouped = shows.stream()
                .collect(Collectors.groupingBy(s -> new MovieCityKey(
                        s.getMovie().getMovieId(),
                        s.getScreen().getTheatre().getCity().getCityId())));

        List<Movie_City_Document> movieCityDocs = grouped.entrySet().stream()
                .map(e -> {
                    var key = e.getKey();
                    var groupShows = e.getValue();
                    Movie movie = groupShows.get(0).getMovie();
                    City city = groupShows.get(0).getScreen().getTheatre().getCity();

                    Instant earliest = groupShows.stream()
                            .map(Show::getStartTime)
                            .min(Instant::compareTo)
                            .orElse(null);

                    List<String> availableFormats = groupShows.stream()
                            .map(s -> s.getScreen().getScreenType())
                            .distinct()
                            .toList();

                    // flat theatre-id list — used only for filtering (theatreId= on /movies),
                    // never for display; names/ratings for browse-by-cinema come from
                    // TheatreController, not this document
                    List<UUID> theatreIds = groupShows.stream()
                            .map(s -> s.getScreen().getTheatre().getTheatreId())
                            .distinct()
                            .toList();

                    MovieStatus cityStatus = deriveCityStatus(earliest);

                    return Movie_City_Document.builder()
                            .id(key.movieId() + "_" + key.cityId())
                            .movieId(key.movieId())
                            .cityId(key.cityId())
                            .cityName(city.getCityName())
                            .title(movie.getTitle())
                            .posterUrl(movie.getPosterUrl())
                            .releasedAt(movie.getReleasedAt())
                            .language(movie.getLanguage())
                            .certificate(movie.getCertificate().name())
                            .rating(parseRatingSafe(movie.getRating()))
                            .genre(movie.getGenre())
                            .cityStatus(cityStatus)
                            .availableFormats(availableFormats)
                            .theatreIds(theatreIds)
                            .earliestShowTime(earliest)
                            .showCount(groupShows.size())
                            .updatedAt(Instant.now())
                            .build();
                })
                .toList();

        movieCityDocumentRepository.saveAll(movieCityDocs);
        return movieCityDocs;
    }

    /**
     * Derives city-scoped Now Showing / Coming Soon status from actual show times,
     * independent of Movie.status (the global editorial flag).
     *
     * Uses a rolling window rather than a strict calendar-day cutoff, so a show
     * scheduled for later tonight doesn't flip to COMING_SOON purely because
     * midnight passed.
     */
    private MovieStatus deriveCityStatus(Instant earliestShowTime) {
        if (earliestShowTime == null) {
            return MovieStatus.COMING_SOON;
        }
        Instant windowEnd = Instant.now().plus(NOW_SHOWING_WINDOW);
        return earliestShowTime.isBefore(windowEnd) ? MovieStatus.NOW_SHOWING : MovieStatus.COMING_SOON;
    }

    public List<Movie_Document>  buildMovieDocument(List<Movie> movies){
        List<Movie_Document> movieDocuments = movies.stream().map(
                m->Movie_Document.builder()
                        .movieId(m.getMovieId())
                        .title(m.getTitle())
                        .description(m.getDescription())
                        .releasedAt(m.getReleasedAt())
                        .language(m.getLanguage())
                        .certificate(m.getCertificate())
                        .posterUrl(m.getPosterUrl())
                        .rating(parseRatingSafe(m.getRating()))
                        .genre(m.getGenre())
                        .status(m.getStatus())
                        .createdAt(m.getCreatedAt())
                        .build()
        ).toList();
        movieDocumentRepository.saveAll(movieDocuments);
        return movieDocuments;
    }

    private Float parseRatingSafe(String rating) {
        try { return Float.parseFloat(rating); }
        catch (Exception e) { return null; }
    }
    public void reindexAll(){

        // Logic to reindex all data from the database to Elasticsearch
        // This would typically involve fetching all relevant data from the database,
        // transforming it into the appropriate document format, and then saving it to Elasticsearch.
        List<Show> shows = showRepository.findAllActiveShowsWithDetails();
        List<Show_Document> showDocuments =
                buildShowDocuments(shows);
        List<Movie_City_Document> movieCityDocuments =
                buildMovieCityDocuments(shows);

        List<Movie> movies = movieRepository.findAll();
        List<Movie_Document> movieDocuments = buildMovieDocument(movies);
        //indexComingSoonMovies()
        ;
    }
}
