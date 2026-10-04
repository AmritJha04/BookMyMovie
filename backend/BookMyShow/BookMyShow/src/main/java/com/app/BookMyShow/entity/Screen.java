package com.app.BookMyShow.entity;

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
public class Screen {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "uuid", columnDefinition = "UUID")
    @JdbcType(UUIDJdbcType.class)
    private UUID screenId ;

    @NotNull
    @JoinColumn(nullable = false)
    @ManyToOne
    private Theatre theatre ;

    @NotNull
    @JoinColumn(nullable = false)
    private String name ;

    @NotNull
    @JoinColumn(nullable = false)
    private int capacity ;

    @NotNull
    @JoinColumn(nullable = false)
    private String screenType ;


}
