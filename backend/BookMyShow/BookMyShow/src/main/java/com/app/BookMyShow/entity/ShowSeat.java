package com.app.BookMyShow.entity;

import com.app.BookMyShow.enums.ShowSeatStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.antlr.v4.runtime.misc.NotNull;
import org.hibernate.annotations.JdbcType;
import org.hibernate.type.descriptor.jdbc.UUIDJdbcType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ShowSeat {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="uuid",columnDefinition = "UUID")
    @JdbcType(UUIDJdbcType.class)
    private UUID showSeatId ;



    @ManyToOne
    @NotNull
    @JoinColumn(nullable = false)
    private Show show ;

    @ManyToOne
    @NotNull
    @JoinColumn(nullable = false)
    private Seat seat;

    @NotNull
    @Column(nullable = false)
    private BigDecimal price;

    @NotNull
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ShowSeatStatus showSeatStatus ;

    @NotNull
    @Column(nullable=false)
    private Instant createdAt ;


}
