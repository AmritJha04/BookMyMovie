package com.app.BookMyShow.entity;

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
@NoArgsConstructor
@AllArgsConstructor
public class Theatre {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "uuid", columnDefinition = "UUID")
    @JdbcType(UUIDJdbcType.class)
    private UUID theatreId ;

    @ManyToOne
    @JoinColumn(name = "city", nullable = false)
    @NotNull
    private City city ;

    @NotNull
    @JoinColumn(nullable = false)
    private String theatreName ;

    @NotNull
    @JoinColumn(nullable = false)
    private String address ;

    @NotNull
    @JoinColumn(nullable = false)
    private String rating ;

    @NotNull
    @JoinColumn(nullable = false)
    private Instant createdAt ;
}
