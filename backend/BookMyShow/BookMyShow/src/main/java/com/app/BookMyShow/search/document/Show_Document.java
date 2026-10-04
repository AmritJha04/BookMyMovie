package com.app.BookMyShow.search.document;

import com.app.BookMyShow.enums.ShowStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "show_listing")
public class Show_Document {

    @Id
    private UUID showId; // Maps directly to your unique show identifier

    @Field(type = FieldType.Keyword)
    private UUID movieId;

    @Field(type = FieldType.Keyword)
    private String movieTitle;

    @Field(type = FieldType.Keyword)
    private UUID cityId;


    @Field(type = FieldType.Keyword)
    private UUID theatreId;

    @Field(type = FieldType.Keyword)
    private String theatreName;


    @Field(type = FieldType.Keyword)
    private UUID screenId;

    @Field(type = FieldType.Keyword)
    private String screenName ;

    @Field(type = FieldType.Date)
    private Instant startTime;

    @Field(type = FieldType.Date)
    private Instant endTime;

    @Field(type = FieldType.Keyword)
    private String format ;

    @Field(type = FieldType.Date,format= DateFormat.date)
    private LocalDate showDate; // Precomputed string format: "YYYY-MM-DD" for fast keyword aggregation

    @Field(type = FieldType.Keyword)
    private ShowStatus showStatus; // String representation of your show status Enum


}
