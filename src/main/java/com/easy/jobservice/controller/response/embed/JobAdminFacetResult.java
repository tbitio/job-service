package com.easy.jobservice.controller.response.embed;

import com.easy.jobservice.controller.response.embed.facet.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
public class JobAdminFacetResult implements Serializable {

    private List<LabelCount> label;
    private List<CityCount> city;
    private List<GeoTypeCount> type;
    private List<UserCount> user;
    private List<StatusCount> status;

    @JsonIgnore
    private List<JobCount> cnt;

}
