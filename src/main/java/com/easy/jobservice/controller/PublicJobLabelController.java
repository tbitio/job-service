package com.easy.jobservice.controller;

import com.easy.jobservice.service.JobLabelService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@Api(value = "REST API for managing jobs labels.")
@RestController
@RequestMapping("/api/v1/public/job-labels")
public class PublicJobLabelController {

    private final JobLabelService jobLabelService;

    @Autowired
    public PublicJobLabelController(JobLabelService jobLabelService) {
        this.jobLabelService = jobLabelService;
    }

    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success. Returns a list of the existing job labels.")
    })
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity find(@RequestParam(value = "name", required = false) String name) {
        Set<String> response = jobLabelService.find(name);
        return ResponseEntity.ok(response);
    }
}
