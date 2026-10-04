package com.app.BookMyShow.service;


import com.app.BookMyShow.dto.ShowSeatResponseDto;
import com.app.BookMyShow.dto.ShowSeatsListResponse;
import com.app.BookMyShow.entity.Show;
import com.app.BookMyShow.entity.ShowSeat;
import com.app.BookMyShow.repository.ShowRepository;
import com.app.BookMyShow.repository.ShowSeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ShowSeatService {

    @Autowired
    private ShowRepository showRepository;

    @Autowired
    private ShowSeatRepository showSeatRepository;

    @Transactional(readOnly = true)
    public ShowSeatsListResponse getSeatsForShow(UUID showId) {
       // System.out.println("before");
        Show show = showRepository.findById(showId)
                .orElseThrow(() -> new ResourceNotFoundException("Show not found: " + showId));
       // System.out.println("before");
        List<ShowSeat> showSeats = showSeatRepository.findAllForShow(showId);

        List<ShowSeatResponseDto> seatDtos = showSeats.stream()
                .map(ss -> new ShowSeatResponseDto(
                        ss.getShowSeatId(),
                        ss.getSeat().getSeatId(),
                         ss.getSeat().getSeatNo(),
                        ss.getSeat().getRowNo(),
                        ss.getSeat().getSeatType(),
                        ss.getPrice(),
                        ss.getShowSeatStatus()
                ))
                .toList();

        return new ShowSeatsListResponse(
                show.getShowId(),

                show.getScreen().getScreenId(),
                show.getScreen().getName(),
                show.getScreen().getTheatre().getTheatreName(),
                show.getMovie().getPosterUrl(),
                show.getScreen().getScreenType(),
                show.getMovie().getTitle(),
                LocalTime.ofInstant(show.getStartTime(), ZoneId.of("Asia/Kolkata")),
                seatDtos
        );
    }
}