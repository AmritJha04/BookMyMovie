package com.app.BookMyShow.dto;


import com.app.BookMyShow.enums.ShowSeatStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class HeldSeatDto {

    private UUID showSeatId ;

    private ShowSeatStatus status ;

}
