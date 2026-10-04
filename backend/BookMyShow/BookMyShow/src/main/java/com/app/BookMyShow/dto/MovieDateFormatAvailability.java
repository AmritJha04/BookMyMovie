package com.app.BookMyShow.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor@Getter@Setter
public class MovieDateFormatAvailability {

    List<LocalDate> dates;
    List<String> formats;

}
