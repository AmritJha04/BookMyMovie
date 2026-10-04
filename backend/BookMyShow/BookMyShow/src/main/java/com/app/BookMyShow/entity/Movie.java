package com.app.BookMyShow.entity;

import com.app.BookMyShow.enums.Genre;
import com.app.BookMyShow.enums.MovieCertificate;
import com.app.BookMyShow.enums.MovieStatus;
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
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "uuid", columnDefinition = "UUID")
    @JdbcType(UUIDJdbcType.class)
    private UUID movieId ;

    @NotNull
    @Column(nullable = false)
    private String title ;

    @NotNull
    @Column(nullable = false,columnDefinition = "TEXT")
    private String description ;

    @NotNull
    @Column(nullable = false)
    private Instant releasedAt ;

    @NotNull
    @Column(nullable = false)
    private String language ;

    @NotNull
    @JoinColumn(nullable = false)
    @Enumerated(EnumType.STRING)
    private MovieCertificate certificate ;

    @NotNull
    @Column(nullable = false)
    private String posterUrl ;

    @NotNull
    @Column(nullable = false)
    private String rating ;

    @NotNull
    @JoinColumn(nullable = false)
    @Enumerated(EnumType.STRING)
    private Genre genre;

    @NotNull
    @JoinColumn(nullable = false)
    @Enumerated(EnumType.STRING)
    private MovieStatus status ;

    @NotNull
    @Column(nullable = false)
    private Instant createdAt ;


}
