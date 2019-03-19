package com.easy.jobservice.controller.response;

import com.easy.jobservice.controller.response.embed.JobAdminFacetResult;
import com.easy.jobservice.dto.JobDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class JobAdminSearchResponse extends JobListResponse<JobDto> {

    private JobAdminFacetResult facets;

}
