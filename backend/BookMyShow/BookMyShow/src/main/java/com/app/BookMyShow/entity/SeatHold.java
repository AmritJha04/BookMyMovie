package com.app.BookMyShow.entity;

import com.app.BookMyShow.enums.HoldStatus;
import com.fasterxml.jackson.databind.annotation.EnumNaming;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.antlr.v4.runtime.misc.NotNull;
import org.hibernate.annotations.JdbcType;
import org.hibernate.type.descriptor.jdbc.UUIDJdbcType;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SeatHold {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "uuid", columnDefinition = "UUID")
    @JdbcType(UUIDJdbcType.class)
    private UUID seatHoldId ;

    @NotNull
    @JoinColumn(nullable = false)
    @ManyToOne
    private ShowSeat showSeat ;

    @NotNull
    @Column(nullable = false)
    private String sessionId ;

    // Add to SeatHold:
    @NotNull
    @Column(nullable = false)
    private UUID holdGroupId;

    @NotNull
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private HoldStatus holdStatus ;

    @NotNull
    @Column(nullable = false)
    private Instant createdAt ;

    @NotNull
    @Column(nullable = false)
    private Instant expiresAt ;

}
