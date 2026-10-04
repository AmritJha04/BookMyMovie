package com.app.BookMyShow.entity;

import com.app.BookMyShow.enums.UserType;
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
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "uuid", columnDefinition = "UUID")
    @JdbcType(UUIDJdbcType.class)
    private UUID userId ;

    @NotNull
    @JoinColumn(nullable = false)
    private String userName ;

    @NotNull
    @JoinColumn(nullable = false)
    private String emailId ;

    @NotNull
    @JoinColumn(nullable = false)
    private UserType userType ;

    @NotNull
    @JoinColumn(nullable = false)
    private Instant createdAt ;

}
