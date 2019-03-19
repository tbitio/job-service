package com.easy.jobservice.controller.response.embed.facet;

import com.easy.jobservice.common.JobGeoType;
import lombok.*;

@Data
public class GeoTypeCount {

    public JobGeoType geoType;
    public long n = 0;

}
