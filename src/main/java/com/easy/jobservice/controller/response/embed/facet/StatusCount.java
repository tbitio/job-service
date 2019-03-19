package com.easy.jobservice.controller.response.embed.facet;

import com.easy.jobservice.common.JobStatus;
import lombok.Data;

@Data
public class StatusCount {

    public JobStatus status;
    public long n = 0;

}
