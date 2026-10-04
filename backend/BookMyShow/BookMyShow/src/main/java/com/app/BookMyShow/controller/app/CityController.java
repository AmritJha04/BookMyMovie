package com.app.BookMyShow.controller.app;


import com.app.BookMyShow.dto.CityDto;
import com.app.BookMyShow.service.CityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping
@CrossOrigin(origins = "http://localhost:5173",allowCredentials = "true")
public class CityController {

    @Autowired
    private CityService cityService;

    @GetMapping(value="/getCities" )
    public ResponseEntity<List<CityDto>> getCities(@RequestParam (required = false) String cityName , @RequestParam (required = false) String cityId, @RequestParam (required = false) String query ,
                                                   @RequestParam(defaultValue = "20") int limit){
        try{
            List<CityDto> cityList = cityService.getAllCities(cityName , cityId , query ,  limit) ;
            return ResponseEntity.status(HttpStatus.OK).body(cityList);
        }
        catch(Exception e){
            System.out.println("Exception Occured");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }


}
