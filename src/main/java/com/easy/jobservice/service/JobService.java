package com.easy.jobservice.service;

import com.easy.jobservice.controller.request.JobAdminSearchRequest;
import com.easy.jobservice.controller.request.JobPublicSearchRequest;
import com.easy.jobservice.controller.response.JobAdminSearchResponse;
import com.easy.jobservice.controller.response.JobPublicSearchResponse;
import com.easy.jobservice.dto.JobDto;
import com.easy.jobservice.entity.User;
import com.easy.jobservice.exception.JobException;
import com.easy.jobservice.exception.NoPermissionException;
import com.easy.jobservice.exception.ObjectNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * The {@code JobService} interface provides methods in order to manage jobs.
 */
public interface JobService {

    /**
     * Returns a list of existing jobs.
     *
     * @param pageable - a {@link Pageable} object describes the pagination information.
     * @return a list of existing jobs.
     */
    Page<JobDto> find(Pageable pageable);

    /**
     * Returns a list of existing jobs for user.
     *
     * @param owner - a {@code String} value specifying the owner of a job.
     * @param pageable - a {@link Pageable} object describes the pagination information.
     * @return a list of existing jobs for user.
     */
    Page<JobDto> find(String owner, Pageable pageable);

    /**
     * Returns job by the specified 'jobID'.
     *
     * @param jobID a {@code String} value specifying the identifier of a job.
     * @return a job by specified 'jobID'.
     * @throws ObjectNotFoundException if job does not exist with such {@code jobID}.
     */
    JobDto find(String jobID) throws ObjectNotFoundException;

    /**
     * Returns jobs by filter.
     *
     * @param request a {@link JobAdminSearchRequest} object.
     * @return a {@link JobAdminSearchResponse} object.
     */
    JobAdminSearchResponse search(JobAdminSearchRequest request);

    /**
     * Returns jobs by filter.
     *
     * @param request a {@link JobPublicSearchRequest} object.
     * @return a {@link JobPublicSearchResponse} object.
     */
    JobPublicSearchResponse search(JobPublicSearchRequest request);

    /**
     * Creates and returns a new job.
     *
     * @param jobDto a {@link JobDto} object.
     * @param user a {@link User} object.
     * @return a {@link JobDto} object.
     */
    JobDto save(JobDto jobDto, User user);

    /**
     * Updates and returns updated job by user.
     *
     * @param jobID a {@code String} value specifying the identifier of a job.
     * @param jobDto a {@link JobDto} object.
     * @param owner a {@code String} value specifying the owner of a job.
     * @return a {@link JobDto} object.
     * @throws ObjectNotFoundException if job does not exist with such {@code jobID}.
     * @throws NoPermissionException if user does not have access rights for updating a job.
     */
    JobDto update(String jobID, String owner, JobDto jobDto)
            throws ObjectNotFoundException, NoPermissionException, JobException;

    /**
     * Updates and returns updated job by admin.
     *
     * @param jobID a {@code String} value specifying the identifier of a job.
     * @param jobDto a {@link JobDto} object.
     * @return a {@link JobDto} object.
     * @throws ObjectNotFoundException if job does not exist with such {@code jobID}.
     */
    JobDto update(String jobID, JobDto jobDto) throws ObjectNotFoundException, JobException;

    /**
     * Deletes job by 'jobID'.
     *
     * @param jobID a {@code String} value specifying the identifier of a job.
     * @throws ObjectNotFoundException if job does not exist with such {@code jobID}.
     */
    void delete(String jobID) throws ObjectNotFoundException;

    /**
     * Deletes job by 'jobID'.
     *
     * @param jobID a {@code String} value specifying the identifier of a job.
     * @param owner a {@code String} value specifying the owner of a job.
     * @throws ObjectNotFoundException if job does not exist with such {@code jobID}.
     * @throws NoPermissionException if user does not have access rights for deleting a job.
     */
    void delete(String jobID, String owner) throws ObjectNotFoundException, NoPermissionException;

}
