package com.app.BookMyShow.search.document;

import com.app.BookMyShow.enums.Genre;
import com.app.BookMyShow.enums.MovieCertificate;
import com.app.BookMyShow.enums.MovieStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.antlr.v4.runtime.misc.NotNull;
import org.hibernate.annotations.JdbcType;
import org.hibernate.type.descriptor.jdbc.UUIDJdbcType;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

import java.time.Instant;
import java.util.UUID;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "movie_listing")
public class Movie_Document {

    @Id
    private UUID movieId ;

    @MultiField(
            mainField = @Field(type = FieldType.Text, analyzer = "standard"),
            otherFields = {
                    @InnerField(suffix = "raw", type = FieldType.Keyword)
            }
    )
    private String title ;

    @Field(type = FieldType.Text)
    private String description ;

    @Field(type = FieldType.Date)
    private Instant releasedAt ;

    @Field(type = FieldType.Keyword)
    private String language ;

    @Field(type = FieldType.Keyword)
    private MovieCertificate certificate ;

    @Field(type = FieldType.Keyword, index = false)
    private String posterUrl ;

    @Field(type = FieldType.Float)
    private Float rating ;

    @Field(type = FieldType.Keyword)
    private Genre genre;

    @Field(type = FieldType.Keyword)
    private MovieStatus status ;

    @Field(type = FieldType.Date)
    private Instant createdAt ;
}
