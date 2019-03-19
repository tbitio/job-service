package com.easy.jobservice.repository;

import com.easy.jobservice.entity.JobLabel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobLabelRepository extends MongoRepository<JobLabel, String> {

    List<JobLabel> findByNameLikeIgnoreCase(String name);

}
