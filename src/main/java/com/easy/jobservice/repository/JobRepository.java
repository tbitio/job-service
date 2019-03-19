package com.easy.jobservice.repository;

import com.easy.jobservice.common.JobStatus;
import com.easy.jobservice.entity.Job;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobRepository extends MongoRepository<Job, String> {

    Page<Job> findByStatus(Pageable pageable, JobStatus status);

    Page<Job> findByCreatedBy(Pageable pageable, String createdBy);

}
