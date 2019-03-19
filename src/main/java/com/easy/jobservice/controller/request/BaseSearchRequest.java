package com.easy.jobservice.controller.request;

import com.easy.jobservice.common.JobGeoType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseSearchRequest {

    private Double distance = 100D;
    private Double lat;
    private Double lon;

    private String search;

    private List<String> labels;
    private JobGeoType jobGeoType = JobGeoType.ALL;

    private String sortBy;
    private Sort.Direction direction = Sort.Direction.DESC;

    private Integer page = 0;
    private Integer limit = 20;

    public void normalize() {
        if (this.limit <=0 || this.limit > 100) {
            this.limit=20;
        }

        if (this.page < 0) {
            this.page = 0;
        }

        if (this.distance < 0) {
            this.distance = 100D;
        }

        if (this.jobGeoType == null) {
            this.jobGeoType = JobGeoType.ALL;
        }

        if (this.labels != null && !this.labels.isEmpty()) {
            this.labels = this.labels.stream()
                    .filter(StringUtils::isNotEmpty)
                    .map(String::trim).collect(Collectors.toList());
        }

        if (this.direction == null) {
            this.direction = Sort.Direction.DESC;
        }
    }
}
