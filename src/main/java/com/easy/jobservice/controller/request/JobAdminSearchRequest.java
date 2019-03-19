package com.easy.jobservice.controller.request;

import com.easy.jobservice.common.JobStatus;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobAdminSearchRequest extends BaseSearchRequest {

    private JobStatus status;
    private String username;

}
