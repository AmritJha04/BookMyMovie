package com.app.BookMyShow.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.antlr.v4.runtime.misc.NotNull;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CreateShowRequest {

    @NotNull
    private UUID movieId ;

    @NotNull
    private UUID screenId ;

    @NotNull
    private Instant startTime ;

    @NotNull
    private Instant endTime ;

    @NotNull
    BigDecimal classicPrice ;

    @NotNull
    BigDecimal premiumPrice ;

    @NotNull
    BigDecimal reclinerPrice ;

}
