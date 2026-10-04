package com.app.BookMyShow.dto;

import com.app.BookMyShow.enums.SeatType;
import com.app.BookMyShow.enums.ShowSeatStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ShowSeatResponseDto {

     private UUID showSeatId ;

     private UUID seatId ;

     private Integer seatNumber ;

     private String rowNumber ;

     private SeatType seatType ;

     private BigDecimal price ;

     private ShowSeatStatus status ;

}
