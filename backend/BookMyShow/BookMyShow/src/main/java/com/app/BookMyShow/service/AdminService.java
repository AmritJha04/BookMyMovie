package com.app.BookMyShow.service;


import com.app.BookMyShow.Exceptions.ShowOverlapException;
import com.app.BookMyShow.dto.CreateShowRequest;
import com.app.BookMyShow.dto.ShowResponse;
import com.app.BookMyShow.entity.*;
import com.app.BookMyShow.enums.SeatType;
import com.app.BookMyShow.enums.ShowSeatStatus;
import com.app.BookMyShow.enums.ShowStatus;
import com.app.BookMyShow.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    @Autowired
    private final ShowRepository showRepository;

    @Autowired
    private final MovieRepository movieRepository;

    @Autowired
    private final ScreenRepository screenRepository;

    @Autowired
    private final SeatRepository seatRepository;

    @Autowired
    private final ShowSeatRepository showSeatRepository;

    @Transactional
    public ShowResponse createShow(CreateShowRequest request) throws Exception {
        Movie movie = movieRepository.findById(request.getMovieId())
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found: " + request.getMovieId()));

        Screen screen = screenRepository.findById(request.getScreenId())
                .orElseThrow(() -> new ResourceNotFoundException("Screen not found: " + request.getScreenId()));

        if (!request.getEndTime().isAfter(request.getStartTime())) {
            throw new IllegalArgumentException("endTime must be after startTime");
        }
        boolean overlaps = showRepository.existsOverlappingShow(
                request.getScreenId(), request.getStartTime(), request.getEndTime());

        if (overlaps) {
            throw new ShowOverlapException(
                    "Screen " + request.getScreenId() + " already has a show scheduled in that window");
        }


        Show show = new Show();
        show.setMovie(movie);
        show.setScreen(screen);
        show.setStartTime(request.getStartTime());
        show.setEndTime(request.getEndTime());
        show.setShowStatus(ShowStatus.SCHEDULED);
        show.setClassicPrice(request.getClassicPrice());
        show.setPremiumPrice(request.getPremiumPrice());
        show.setReclinerPrice(request.getReclinerPrice());
        show.setCreatedAt(Instant.now());
        show = showRepository.save(show);

        int seatsGenerated = fanOutShowSeats(show);

        return new ShowResponse(
                show.getShowId(), movie.getMovieId(), screen.getScreenId(),
                show.getStartTime(), show.getEndTime(), show.getShowStatus().name(),
                show.getClassicPrice(), show.getPremiumPrice(), show.getReclinerPrice(),
                seatsGenerated
        );
    }

    private int fanOutShowSeats(Show show) {
        List<Seat> seats = seatRepository.findAllByScreen_ScreenId(show.getScreen().getScreenId());

        if (seats.isEmpty()) {
            throw new IllegalStateException(
                    "Screen " + show.getScreen().getScreenId() + " has no seats configured");
        }

        Instant now = Instant.now();
        List<ShowSeat> showSeats = seats.stream()
                .map(seat -> {
                    ShowSeat ss = new ShowSeat();
                    ss.setShow(show);
                    ss.setSeat(seat);
                    ss.setPrice(priceFor(show, seat.getSeatType()));
                    ss.setShowSeatStatus(ShowSeatStatus.AVAILABLE);
                    ss.setCreatedAt(now);
                    return ss;
                })
                .toList();

        showSeatRepository.saveAll(showSeats);
        return showSeats.size();
    }

    private BigDecimal priceFor(Show show, SeatType seatType) {
        return switch (seatType) {
            case CLASSIC -> show.getClassicPrice();
            case PREMIUM -> show.getPremiumPrice();
            case RECLINER -> show.getReclinerPrice();
        };
    }
}