package com.easy.jobservice.entity;

import com.easy.jobservice.common.BaseEntity;
import com.easy.jobservice.common.JobGeoType;
import com.easy.jobservice.common.JobStatus;
import com.easy.jobservice.common.JobType;
import com.easy.jobservice.utils.Utils;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.TextScore;

import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.List;

@Getter
@Setter
@Document(collection = "job")
public class Job extends BaseEntity<String> {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @NotNull
    @Field("name")
    @TextIndexed(weight = 3)
    private String name;

    @Field("city")
    private String city;

    @Field("country")
    private String country;

    @Field("placeName")
    private String placeName;

    @Field("placeId")
    private String placeId;

    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
    private GeoJsonPoint geoPoint;

    @NotNull
    @Field("telegram_group_url")
    private String telegramGroupUrl;

    @Field("price")
    private Double price;

    @TextIndexed
    @Field("description")
    private String description;

    @Indexed
    @Field("label")
    private List<String> label;

    @Field("photos")
    private List<JobPhoto> photos;

    @Field("deadline")
    private Instant deadline;

    @Field("type")
    private JobType type;

    @Field("status")
    private JobStatus status;

    @Indexed
    private JobGeoType geoType = JobGeoType.ON_SITE;

    @Field("photo")
    private String photo;

    @TextScore
    private transient Float score;

    @Field("owner_name")
    private String ownerName;

    public void setLocation(Double lat, Double lon) {
        if (lat != null && lon != null && Utils.isValidLocationArray(lat, lon)) {
            this.geoPoint = new GeoJsonPoint(lat, lon);
        } else {
            this.geoPoint = null;
        }
    }
}
