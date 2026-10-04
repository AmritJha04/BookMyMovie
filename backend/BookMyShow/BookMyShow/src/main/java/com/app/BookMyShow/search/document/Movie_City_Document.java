package com.app.BookMyShow.search.document;

import com.app.BookMyShow.enums.Genre;
import com.app.BookMyShow.enums.MovieStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.InnerField;
import org.springframework.data.elasticsearch.annotations.MultiField;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "movie_city_listing")
public class Movie_City_Document {

    @Id
    private String id; // Composite key format: "movieId_cityId"

    @Field(type = FieldType.Keyword)
    private UUID movieId;

    @Field(type = FieldType.Keyword)
    private UUID cityId;

    @Field(type = FieldType.Keyword)
    private String cityName;

    // Maps the text field with its "raw" keyword sub-field
    @MultiField(
            mainField = @Field(type = FieldType.Text, analyzer = "standard"),
            otherFields = {
                    @InnerField(suffix = "raw", type = FieldType.Keyword)
            }
    )
    private String title;

  //  @Field(type = FieldType.Text)
   // private String description;

    @Field(type = FieldType.Keyword, index = false)
    private String posterUrl;

    @Field(type = FieldType.Date)
    private Instant releasedAt;

    @Field(type = FieldType.Keyword)
    private String language; // Using standard string representation of your Enum

    @Field(type = FieldType.Keyword)
    private String certificate; // Using standard string representation of your Enum

    @Field(type = FieldType.Float)
    private Float rating;

    @Field(type = FieldType.Keyword)
    private Genre genre; // Using standard string representation of your Enum

    @Field(type = FieldType.Keyword)
    private MovieStatus cityStatus; // NOW_SHOWING or COMING_SOON

    // Unique list of screens formats (e.g., ["2D", "3D", "IMAX"])

    @Field(type = FieldType.Date)
    private Instant earliestShowTime;

    @Field(type = FieldType.Keyword)
    private List<String> availableFormats; // this is screen-type derived from show table

    @Field(type = FieldType.Integer)
    private Integer showCount;

    @Field(type = FieldType.Keyword)
    private List<UUID> theatreIds;

    @Field(type = FieldType.Date)
    private Instant updatedAt;
}
