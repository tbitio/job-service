package com.easy.jobservice.controller;

import com.easy.jobservice.controller.request.JobAdminSearchRequest;
import com.easy.jobservice.controller.response.JobAdminSearchResponse;
import com.easy.jobservice.controller.response.JobUploadPhotoResponse;
import com.easy.jobservice.dto.ErrorResponse;
import com.easy.jobservice.dto.JobDto;
import com.easy.jobservice.entity.User;
import com.easy.jobservice.exception.JobException;
import com.easy.jobservice.exception.NoPermissionException;
import com.easy.jobservice.exception.ObjectNotFoundException;
import com.easy.jobservice.service.JobService;
import com.easy.jobservice.service.UploadService;
import com.easy.jobservice.utils.Constants;
import com.easy.jobservice.utils.SecurityUtils;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Principal;
import java.util.Date;

@Slf4j
@Api(value = "Private REST API for managing jobs.")
@RestController
@RequestMapping("/api/v1/private")
public class PrivateJobController {

    private final JobService jobService;
    private final UserDetailsService userDetailsService;
    private final UploadService uploadService;

    @Autowired
    public PrivateJobController(JobService jobService, UserDetailsService userDetailsService,
                                 UploadService uploadService) {

        this.jobService = jobService;
        this.userDetailsService = userDetailsService;
        this.uploadService = uploadService;
    }

    @ApiImplicitParam(name = "Authorization", dataType = "string", paramType = "header", required = true)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success. Returns a list of existing jobs.")
    })
    @RequestMapping(value = "/jobs/search", method = RequestMethod.GET)
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity search(JobAdminSearchRequest searchRequest) {
        log.debug("Private REST API for searching job by filter: {}", searchRequest);
        JobAdminSearchResponse data = jobService.search(searchRequest);

        return ResponseEntity.ok(data);
    }

    @ApiImplicitParam(name = "Authorization", dataType = "string", paramType = "header", required = true)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success. Returns a list of existing jobs by user.")
    })
    @RequestMapping(value = "/jobs", method = RequestMethod.GET)
    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity find(@ApiParam Pageable pageable, Principal principal) {
        log.debug("Private REST API for getting jobs by user: ", principal.getName());
        Page<JobDto> response = jobService.find(principal.getName(), pageable);

        return ResponseEntity.ok(response);
    }

    @ApiImplicitParam(name = "Authorization", dataType = "string", paramType = "header", required = true)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success. Creates and returns a new job."),
            @ApiResponse(code = 400, message = "Failure. Indicates the request body is invalid.")
    })
    @RequestMapping(value = "/jobs", method = RequestMethod.POST)
    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity save(@Valid @RequestBody JobDto jobDto, Principal principal) {
        log.debug("Private REST API for creating a new job: {}", jobDto);

        User user = (User) userDetailsService.loadUserByUsername(principal.getName());
        JobDto newJob = jobService.save(jobDto, user);

        return ResponseEntity.ok(newJob);
    }

    @ApiImplicitParam(name = "Authorization", dataType = "string", paramType = "header", required = true)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success Updates and returns updated job."),
            @ApiResponse(code = 400, message = "Failure. Indicates the request body is invalid."),
            @ApiResponse(code = 403, message = "Failure. Indicates the the user does not have access rights " +
                    "for editing the current job."),
            @ApiResponse(code = 404, message = "Failure. Indicates the requested job does not exist.")
    })
    @RequestMapping(value = "/jobs/{jobID}", method = RequestMethod.PUT)
    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity update(@Valid @RequestBody JobDto jobDto, Principal principal,
                                 @PathVariable String jobID) {
        log.debug("Private REST API for updating the existing job: {}", jobDto);

        try {
            JobDto updatedJob = SecurityUtils.isCurrentUserInRole(Constants.ROLE_ADMIN_STRING) ?
                    jobService.update(jobID, jobDto) :
                    jobService.update(jobID, principal.getName(), jobDto);

            return ResponseEntity.ok(updatedJob);

        } catch (ObjectNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ErrorResponse.builder()
                            .message(String.format("Job not found with such id: %s.", jobID))
                            .status(HttpStatus.NOT_FOUND.value())
                            .timestamp(new Date().getTime())
                            .build()
            );

        } catch (NoPermissionException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    ErrorResponse.builder()
                            .message("The user does not have access rights for updating current job.")
                            .status(HttpStatus.FORBIDDEN.value())
                            .timestamp(new Date().getTime())
                            .build()
            );

        } catch (JobException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ErrorResponse.builder()
                            .message(ex.getMessage())
                            .status(HttpStatus.BAD_REQUEST.value())
                            .timestamp(new Date().getTime())
                            .build()
            );
        }
    }

    @ApiImplicitParam(name = "Authorization", dataType = "string", paramType = "header", required = true)
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Success. Deletes job by 'jobID'."),
            @ApiResponse(code = 403, message = "Failure. Indicates the the user does not have access rights " +
                    "for editing the current job."),
            @ApiResponse(code = 404, message = "Failure. Indicates the requested job does not exist.")
    })
    @RequestMapping(value = "/jobs/{jobID}", method = RequestMethod.DELETE)
    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity delete(@PathVariable String jobID, Principal principal) {
        log.debug("Private REST API for deleting the existing job: {}", jobID);

        try {
            if(SecurityUtils.isCurrentUserInRole(Constants.ROLE_ADMIN_STRING)) {
                jobService.delete(jobID);
            } else {
                jobService.delete(jobID, principal.getName());
            }

            return ResponseEntity.noContent().build();

        } catch (ObjectNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ErrorResponse.builder()
                            .message(String.format("Job not found with such id: %s.", jobID))
                            .status(HttpStatus.NOT_FOUND.value())
                            .timestamp(new Date().getTime())
                            .build()
            );

        } catch (NoPermissionException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    ErrorResponse.builder()
                            .message("The user does not have access rights for updating current job.")
                            .status(HttpStatus.FORBIDDEN.value())
                            .timestamp(new Date().getTime())
                            .build()
            );
        }
    }

    @ApiImplicitParam(name = "Authorization", dataType = "string", paramType = "header", required = true)
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Success. Uploads a new photo for the specified job."),
            @ApiResponse(code = 403, message = "Failure. Indicates the the user does not have access rights " +
                    "for uploading a new photo to the specified job."),
            @ApiResponse(code = 404, message = "Failure. Indicates the requested job does not exist.")
    })
    @RequestMapping(value = "/photo-upload", method = RequestMethod.POST)
    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity uploadPhoto(@RequestPart(value = "file") MultipartFile file) throws Exception {
        File uploadFile = multipartToFile(file);
        File thumbnailFile = uploadService.getThumbnail(uploadFile);

        String url = uploadService.uploadPhoto(uploadFile);
        String thumbnail = thumbnailFile != null ? uploadService.uploadPhoto(thumbnailFile) : null;

        JobUploadPhotoResponse response = JobUploadPhotoResponse.builder()
                .url(url)
                .thumbnail(thumbnail)
                .build();

        return ResponseEntity.ok(response);
    }

    private File multipartToFile(MultipartFile multipart) throws IllegalStateException, IOException {
        File tempFile = File.createTempFile(
                String.format("%s_", FilenameUtils.getBaseName(multipart.getOriginalFilename())),
                String.format(".%s", FilenameUtils.getExtension(multipart.getOriginalFilename()))
        );

        try (FileOutputStream fileOutputStream = new FileOutputStream(tempFile)) {
            fileOutputStream.write(multipart.getBytes());
        }

        return tempFile;
    }
}
