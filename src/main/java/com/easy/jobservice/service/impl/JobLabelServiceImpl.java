package com.easy.jobservice.service.impl;

import com.easy.jobservice.entity.JobLabel;
import com.easy.jobservice.repository.JobLabelRepository;
import com.easy.jobservice.service.JobLabelService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.util.Preconditions.checkNotNull;

@Service
public class JobLabelServiceImpl implements JobLabelService {

    private final JobLabelRepository jobLabelRepository;
    private final MongoOperations mongoOperations;

    @Autowired
    public JobLabelServiceImpl(JobLabelRepository jobLabelRepository, MongoOperations mongoOperations) {
        this.jobLabelRepository = jobLabelRepository;
        this.mongoOperations = mongoOperations;
    }

    @Override
    public Set<String> find(String name) {
        List<JobLabel> labels = StringUtils.isNotEmpty(name) ?
                jobLabelRepository.findByNameLikeIgnoreCase(name) :
                jobLabelRepository.findAll();

        return new HashSet<>(labels).stream()
                .map(jobLabel -> jobLabel.getName())
                .collect(Collectors.toSet());
    }

    @Override
    public void save(List<String> labels) {
        checkNotNull(labels, "Labels cannot be null.");

        Update update = new Update();
        update.inc("count", 1);

        for (String label : labels) {
            Query query = new Query();
            query.addCriteria(Criteria.where("name").is(label.toLowerCase()));
            mongoOperations.upsert(query, update, JobLabel.class).getN();
        }
    }

    @Override
    public void onJobDelete(List<String> labels) {
        checkNotNull(labels, "Labels cannot be null.");

        Query query = new Query();
        query.addCriteria(Criteria.where("name").in(labels.stream()
                .map(String::toLowerCase).collect(Collectors.toList())));

        Update update = new Update();
        update.inc("count", -1);

        mongoOperations.updateMulti(query, update, JobLabel.class).getN();
    }
}
