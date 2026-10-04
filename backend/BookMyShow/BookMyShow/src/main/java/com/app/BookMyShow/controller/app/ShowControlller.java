package com.app.BookMyShow.controller.app;

import com.app.BookMyShow.dto.ShowSeatsListResponse;
import com.app.BookMyShow.service.ShowSeatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

@Controller
@RequestMapping("/shows")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class ShowControlller {

    @Autowired
    private ShowSeatService showSeatService ;

    @GetMapping("/{showId}/seats")
    public ResponseEntity<ShowSeatsListResponse> getSeats(@PathVariable UUID showId) {
      //  System.out.println("reached");
        return ResponseEntity.ok(showSeatService.getSeatsForShow(showId));
    }

}
