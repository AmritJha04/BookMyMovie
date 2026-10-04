package com.app.BookMyShow.service;

import com.app.BookMyShow.dto.CityDto;
import com.app.BookMyShow.repository.CityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.app.BookMyShow.entity.City ;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CityService {

    @Autowired
    private CityRepository cityRepository ;

    public List<CityDto> getAllCities(String cityName, String cityId , String query ,  int limit) {
        Pageable pageable =  PageRequest.of(0, limit);

        List<City> cities ;
        if(query==null) query = "";
        if(cityName == null || cityId == null){
            cities = cityRepository.findFirstCities(pageable , query);
        }

        else{
            UUID uuid = UUID.fromString(cityId);
            cities = cityRepository.findNextCities(cityName, uuid, query , pageable);
        }
      
        List<CityDto> cityDtos = cities.stream()
                .map(city -> {
                    CityDto dto = new CityDto();
                    dto.setCityName(city.getCityName());
                    dto.setState(city.getState());
                    dto.setCityId(city.getCityId().toString());
                    return dto;
                })
                .collect(Collectors.toList());

        return cityDtos;
    }
}
