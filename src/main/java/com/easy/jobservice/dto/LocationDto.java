package com.easy.jobservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LocationDto {

    private String city;
    private String country;

    private Double lat;
    private Double lon;

    private String placeName;
    private String placeId;

}
