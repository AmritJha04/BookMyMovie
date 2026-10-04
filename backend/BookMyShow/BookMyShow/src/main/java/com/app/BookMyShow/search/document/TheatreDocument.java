package com.app.BookMyShow.search.document;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.UUID;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TheatreDocument {

    @Id
    private UUID theatreId;

    @Field(type = FieldType.Keyword)
    private String theatreName;

    @Field(type = FieldType.Keyword)
    private String rating;
}
