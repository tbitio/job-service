package com.easy.jobservice.dto;

import com.easy.jobservice.common.JobGeoType;
import com.easy.jobservice.common.JobStatus;
import com.easy.jobservice.common.JobType;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@AllArgsConstructor
@Builder
public class JobDto implements Serializable {

    @ApiModelProperty(hidden=true)
    private String id;

    @NotNull(message = "Missing required parameter 'name'.")
    private String name;

    private LocationDto location;

    private String telegramGroupUrl;

    private Double price;

    private String description;

    private List<String> label = new ArrayList<>();

    private String deadline;

    @ApiModelProperty(hidden=true)
    private JobType type;

    private JobStatus status;

    private List<PhotoDto> photos;

    private JobGeoType geoType = JobGeoType.ON_SITE;

    @ApiModelProperty(hidden=true)
    private String ownerName;

    @ApiModelProperty(hidden=true)
    private String createdBy;

}
