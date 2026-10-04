package com.app.BookMyShow.entity;

import com.app.BookMyShow.enums.ShowStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.antlr.v4.runtime.misc.NotNull;
import org.hibernate.annotations.JdbcType;
import org.hibernate.annotations.NotFound;
import org.hibernate.type.descriptor.jdbc.UUIDJdbcType;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Show {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="uuid",columnDefinition = "UUID")
    @JdbcType(UUIDJdbcType.class)
    private UUID showId ;

    @ManyToOne
    @NotNull
    @JoinColumn(nullable = false)
    private Movie movie ;

    @ManyToOne
    @NotNull
    @JoinColumn(nullable = false)
    private Screen screen ;

    @NotNull
    @JoinColumn(nullable = false)
    private Instant startTime ;

    @NotNull
    @JoinColumn(nullable = false)
    private Instant endTime ;

    @NotNull
    @JoinColumn(nullable = false)
    @Enumerated(EnumType.STRING)
    private ShowStatus showStatus ;



    @NotNull
    @JoinColumn(nullable = false)
    private BigDecimal classicPrice;

    @NotNull
    @JoinColumn(nullable = false)
    private BigDecimal premiumPrice;

    @NotNull
    @JoinColumn(nullable = false)
    private BigDecimal reclinerPrice;

    @NotNull
    @JoinColumn(nullable = false)
    private Instant createdAt ;

}
