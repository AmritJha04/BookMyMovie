package com.app.BookMyShow.controller.app;

import com.app.BookMyShow.dto.TheatreResponse;
import com.app.BookMyShow.service.TheatreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/home")
@CrossOrigin(origins = "http://localhost:5173",allowCredentials = "true")
public class TheatreController {

    @Autowired
    private TheatreService theatreService;

    @GetMapping(value="/getTheatres" )
    public ResponseEntity<List<TheatreResponse>> getAllTheatres(@RequestParam (required = false) String q, @RequestParam (required = false) String cityId){
        try{
            UUID uuid = UUID.fromString(cityId);
            List<TheatreResponse> theatreList = theatreService.getAllTheatres(uuid, q) ;
            return ResponseEntity.status(HttpStatus.OK).body(theatreList);
        }
        catch(Exception e){
            System.out.println("Exception Occured");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
