package com.app.BookMyShow.entity;

import com.app.BookMyShow.enums.SeatType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.antlr.v4.runtime.misc.NotNull;
import org.hibernate.annotations.JdbcType;
import org.hibernate.type.descriptor.jdbc.UUIDJdbcType;

import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "uuid", columnDefinition = "UUID")
    @JdbcType(UUIDJdbcType.class)
    private UUID seatId ;

    @NotNull
    @JoinColumn(nullable = false)
    @ManyToOne
    private Screen screen ;

    @NotNull
    @JoinColumn(nullable = false)
    private String rowNo ;

    @NotNull
    @JoinColumn(nullable = false)
    private int seatNo ;

    @NotNull
    @JoinColumn(nullable = false)
    @Enumerated(EnumType.STRING)
    private SeatType seatType ;
}
