package com.easy.jobservice.controller.response;

import com.easy.jobservice.controller.response.embed.JobFacetResult;
import com.easy.jobservice.dto.JobDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class JobPublicSearchResponse extends JobListResponse<JobDto> {

    private JobFacetResult facets;

}
