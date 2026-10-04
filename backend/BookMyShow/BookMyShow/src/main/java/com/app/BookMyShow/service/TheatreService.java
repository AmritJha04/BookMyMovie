package com.app.BookMyShow.service;

import com.app.BookMyShow.dto.CityDto;
import com.app.BookMyShow.dto.TheatreResponse;
import com.app.BookMyShow.entity.City;
import com.app.BookMyShow.entity.Theatre;
import com.app.BookMyShow.repository.TheatreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TheatreService {

    @Autowired
    private TheatreRepository theatreRepository;

    public List<TheatreResponse> getAllTheatres(
            UUID cityId,
            String q
    ) {
        if (q != null) {
            q = q.trim();
        }

        if (q != null && q.isBlank()) {
            q = null;
        }

        return theatreRepository
                .findTheatresByCity(cityId, q)
                .stream()
                .map(theatre -> {
                    TheatreResponse response = new TheatreResponse();

                    response.setTheatreId(theatre.getTheatreId());
                    response.setTheatreName(theatre.getTheatreName());

                    return response;
                })
                .toList();
    }
}
