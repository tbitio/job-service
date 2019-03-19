package com.easy.jobservice.controller;

import com.easy.jobservice.controller.request.JobPublicSearchRequest;
import com.easy.jobservice.controller.response.JobListResponse;
import com.easy.jobservice.controller.response.JobPublicSearchResponse;
import com.easy.jobservice.dto.ErrorResponse;
import com.easy.jobservice.dto.JobDto;
import com.easy.jobservice.exception.ObjectNotFoundException;
import com.easy.jobservice.service.JobService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@Slf4j
@Api(value = "Public REST API for managing jobs.")
@RestController
@RequestMapping("/api/v1/public/jobs")
public class PublicJobController {

    private final JobService jobService;

    @Autowired
    public PublicJobController(JobService jobService) {
        this.jobService = jobService;
    }

    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success. Search jobs.")
    })
    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public ResponseEntity search(JobPublicSearchRequest searchRequest) {
        log.debug("Public REST API for searching jobs by filter: {}.", searchRequest);

        JobPublicSearchResponse data = jobService.search(searchRequest);
        return ResponseEntity.ok(data);
    }

    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success. Returns all active jobs without facets.")
    })
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity find(@ApiParam Pageable pageable) {
        log.debug("Public REST API for getting existing jobs.");

        Page<JobDto> page = jobService.find(pageable);
        JobListResponse<JobDto> response = new JobListResponse<>();

        response.setTotalElements(page.getTotalElements());
        response.setTotalPages(page.getTotalPages());
        response.setJobs(page.getContent());

        return ResponseEntity.ok(response);
    }

    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success. Returns job by 'jobID'."),
            @ApiResponse(code = 404, message = "Failure. Indicates the requested job does not exist.")
    })
    @GetMapping("/{jobID}")
    public ResponseEntity find(@PathVariable String jobID) {
        log.debug("Public REST API for getting the job by 'jobID': {}", jobID);

        try {
            JobDto job = jobService.find(jobID);
            return ResponseEntity.ok(job);

        } catch (ObjectNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ErrorResponse.builder()
                            .message(String.format("Job not found with such id: %s.", jobID))
                            .status(HttpStatus.NOT_FOUND.value())
                            .timestamp(new Date().getTime())
                            .build()
            );
        }
    }
}
