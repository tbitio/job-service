package com.easy.jobservice.controller.response.embed;

import com.easy.jobservice.controller.response.embed.facet.CityCount;
import com.easy.jobservice.controller.response.embed.facet.LabelCount;
import com.easy.jobservice.controller.response.embed.facet.JobCount;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class JobFacetResult implements Serializable {

    private List<LabelCount> label;
    private List<CityCount> city;

    @JsonIgnore
    private List<JobCount> cnt;


}
