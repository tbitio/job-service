package com.easy.jobservice.service.impl;

import com.easy.jobservice.mapper.JobMapper;
import com.easy.jobservice.common.JobGeoType;
import com.easy.jobservice.common.JobStatus;
import com.easy.jobservice.controller.request.JobAdminSearchRequest;
import com.easy.jobservice.controller.request.JobPublicSearchRequest;
import com.easy.jobservice.controller.response.JobAdminSearchResponse;
import com.easy.jobservice.controller.response.JobPublicSearchResponse;
import com.easy.jobservice.controller.response.embed.JobAdminFacetResult;
import com.easy.jobservice.controller.response.embed.JobFacetResult;
import com.easy.jobservice.dto.JobDto;
import com.easy.jobservice.entity.Job;
import com.easy.jobservice.entity.User;
import com.easy.jobservice.exception.JobException;
import com.easy.jobservice.exception.NoPermissionException;
import com.easy.jobservice.exception.ObjectNotFoundException;
import com.easy.jobservice.repository.JobRepository;
import com.easy.jobservice.service.JobLabelService;
import com.easy.jobservice.service.JobService;
import com.easy.jobservice.utils.Utils;
import com.mongodb.DBObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.FacetOperation;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.query.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;
import static org.springframework.data.domain.Sort.Direction.DESC;

@Slf4j
@Service
public class JobServiceImpl implements JobService {

    private final JobRepository jobRepository;
    private final JobLabelService jobLabelService;
    private final JobMapper jobMapper;
    private final MongoTemplate mongoTemplate;

    @Autowired
    public JobServiceImpl(JobRepository jobRepository, JobLabelService jobLabelService,
                          JobMapper jobMapper, MongoTemplate mongoTemplate) {

        this.jobRepository = jobRepository;
        this.jobLabelService = jobLabelService;
        this.jobMapper = jobMapper;
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Page<JobDto> find(Pageable pageable) {
        return jobRepository.findByStatus(pageable, JobStatus.ACTIVE)
                .map(jobMapper::toDto);
    }

    @Override
    public Page<JobDto> find(String owner, Pageable pageable) {
        return jobRepository.findByCreatedBy(pageable, owner)
                .map(jobMapper::toDto);
    }

    @Override
    public JobDto find(String jobID) throws ObjectNotFoundException {
        Job job = Optional.ofNullable(jobRepository.findOne(jobID))
                .orElseThrow(ObjectNotFoundException::new);

        return jobMapper.toDto(job);
    }

    @Override
    public JobAdminSearchResponse search(JobAdminSearchRequest request) {
        JobAdminSearchResponse response = new JobAdminSearchResponse();
        request.normalize();

        Query query = buildAdminSearchBaseQuery(request);
        DBObject aggregateQueryObject = query.getQueryObject();

        CriteriaDefinition criteriaDefinition = new CriteriaDefinition() {
            @Override
            public DBObject getCriteriaObject() {
                return aggregateQueryObject;
            }

            @Override
            public String getKey() {
                return null;
            }
        };

        if (Utils.isValidLocationArray(request.getLat(), request.getLon())) {
            query.addCriteria(Criteria
                    .where("geoPoint")
                    .nearSphere(new Point(request.getLat(), request.getLon())).maxDistance(new Distance(
                            request.getDistance(),
                            Metrics.KILOMETERS
                    ).getNormalizedValue()));
        }

        // set jobs
        response.setJobs(mongoTemplate.find(query, Job.class)
                .stream()
                .map(jobMapper::toDto)
                .collect(Collectors.toList()));

        JobAdminFacetResult jobFacetResult = getJobAdminFacets(criteriaDefinition);

        if (jobFacetResult.getCnt() != null && !jobFacetResult.getCnt().isEmpty()) {
            response.setTotalElements(jobFacetResult.getCnt().get(0).getCount());
            response.setTotalPages((int) Math.ceil((double) response.getTotalElements() / request.getLimit()));
        }

        // set facets
        response.setFacets(jobFacetResult);

        return response;
    }

    @Override
    public JobPublicSearchResponse search(JobPublicSearchRequest request) {
        JobPublicSearchResponse response = new JobPublicSearchResponse();
        request.normalize();

        Query query = buildPublicSearchBaseQuery(request);
        DBObject aggregateQueryObject = query.getQueryObject();

        CriteriaDefinition criteriaDefinition = new CriteriaDefinition() {
            @Override
            public DBObject getCriteriaObject() {
                return aggregateQueryObject;
            }

            @Override
            public String getKey() {
                return null;
            }
        };

        if (Utils.isValidLocationArray(request.getLat(), request.getLon())) {
            query.addCriteria(Criteria
                    .where("geoPoint")
                    .nearSphere(new Point(request.getLat(), request.getLon())).maxDistance(new Distance(
                            request.getDistance(),
                            Metrics.KILOMETERS
                    ).getNormalizedValue()));
        }

        // set jobs
        response.setJobs(mongoTemplate.find(query, Job.class)
                .stream()
                .map(jobMapper::toDto)
                .collect(Collectors.toList()));

        JobFacetResult jobFacetResult = getJobPublicFacets(criteriaDefinition);

        if (jobFacetResult.getCnt() != null && !jobFacetResult.getCnt().isEmpty()) {
            response.setTotalElements(jobFacetResult.getCnt().get(0).getCount());
            response.setTotalPages((int) Math.ceil((double) response.getTotalElements() / request.getLimit()));
        }

        // set facets
        response.setFacets(jobFacetResult);

        return response;
    }

    @Transactional
    @Override
    public JobDto save(JobDto jobDto, User user) {
        if(jobDto.getLabel() != null) {
            jobLabelService.save(jobDto.getLabel());
        }

        Job job = jobMapper.toEntity(new Job(), jobDto);
        job.setStatus(JobStatus.PENDING);
        job.setOwnerName(user.getName());

        Job newJob = jobRepository.save(job);

        return jobMapper.toDto(newJob);
    }

    @Transactional
    @Override
    public JobDto update(String jobID, String owner, JobDto jobDto)
            throws ObjectNotFoundException, NoPermissionException, JobException {

        Job job = Optional.ofNullable(jobRepository.findOne(jobID))
                .orElseThrow(ObjectNotFoundException::new);

        if(!job.getCreatedBy().equalsIgnoreCase(owner)) {
            throw new NoPermissionException();
        }

        if(jobDto.getLabel() != null) {
            jobLabelService.save(jobDto.getLabel());
        }

        job.setId(jobID);
        updateJobStatus(job, jobDto.getStatus(), false);
        Job updatedJob = jobRepository.save(jobMapper.toEntity(job, jobDto));

        return jobMapper.toDto(updatedJob);
    }

    @Transactional
    @Override
    public JobDto update(String jobID, JobDto jobDto) throws ObjectNotFoundException, JobException {
        Job job = Optional.ofNullable(jobRepository.findOne(jobID))
                .orElseThrow(ObjectNotFoundException::new);

        if(jobDto.getLabel() != null) {
            jobLabelService.save(jobDto.getLabel());
        }

        job.setId(jobID);
        updateJobStatus(job, jobDto.getStatus(), true);

        Job updatedJob = jobRepository.save(jobMapper.toEntity(job, jobDto));

        return jobMapper.toDto(updatedJob);
    }

    @Transactional
    @Override
    public void delete(String jobID) throws ObjectNotFoundException {
        Job job = Optional.ofNullable(jobRepository.findOne(jobID))
                .orElseThrow(ObjectNotFoundException::new);

        if(job.getLabel() != null) {
            jobLabelService.onJobDelete(job.getLabel());
        }

        jobRepository.delete(job.getId());
    }

    @Transactional
    @Override
    public void delete(String jobID, String owner) throws ObjectNotFoundException, NoPermissionException {
        Job job = Optional.ofNullable(jobRepository.findOne(jobID))
                .orElseThrow(ObjectNotFoundException::new);

        if(!job.getCreatedBy().equalsIgnoreCase(owner)) {
            throw new NoPermissionException();
        }

        if(job.getLabel() != null) {
            jobLabelService.onJobDelete(job.getLabel());
        }

        jobRepository.delete(job.getId());
    }

    private Query buildPublicSearchBaseQuery(JobPublicSearchRequest request) {
        Query query = new Query();

        if (StringUtils.isNotEmpty(request.getSearch())) {
            TextCriteria criteria = TextCriteria.forDefaultLanguage()
                    .matchingAny(request.getSearch());
            query = TextQuery.queryText(criteria)
                    .sortByScore();
        }

        query.with(new PageRequest(request.getPage(), request.getLimit()));

        if (!Utils.isValidLocationArray(request.getLat(), request.getLon())) {
            query.with(new Sort(Optional.ofNullable(request.getDirection()).orElse(Sort.Direction.DESC),
                    Optional.ofNullable(request.getSortBy()).orElse("created_date")));
        }

        query.addCriteria(Criteria.where("status").is(JobStatus.ACTIVE))
                .with(new PageRequest(request.getPage(), request.getLimit()));

        if (request.getLabels() != null && !request.getLabels().isEmpty()) {
            query.addCriteria(Criteria.where("label").in(request.getLabels()));
        }

        if(request.getJobGeoType() != null && !request.getJobGeoType().equals(JobGeoType.ALL)) {
            query.addCriteria(Criteria.where("geoType").is(request.getJobGeoType()));
        }

        // add location filter
        addLocationFilter(query, request.getLat(), request.getLon(),
                request.getDistance(), request.getJobGeoType());

        log.debug("Public search query: {}", query);

        return query;
    }

    private Query buildAdminSearchBaseQuery(JobAdminSearchRequest request) {
        Query query = new Query();

        if (StringUtils.isNotEmpty(request.getSearch())) {
            TextCriteria criteria = TextCriteria.forDefaultLanguage()
                    .matchingAny(request.getSearch());
            query = TextQuery.queryText(criteria)
                    .sortByScore();
        }

        query.with(new PageRequest(request.getPage(), request.getLimit()));

        if (!Utils.isValidLocationArray(request.getLat(), request.getLon())) {
            query.with(new Sort(Optional.ofNullable(request.getDirection()).orElse(Sort.Direction.DESC),
                    Optional.ofNullable(request.getSortBy()).orElse("created_date")));
        }

        if (request.getStatus() != null) {
            query.addCriteria(Criteria.where("status").is(request.getStatus()));
        }

        if (request.getLabels() != null && !request.getLabels().isEmpty()) {
            query.addCriteria(Criteria.where("label").in(request.getLabels()));
        }

        if (StringUtils.isNotEmpty(request.getUsername())) {
            query.addCriteria(Criteria.where("createdBy").is(request.getUsername()));
        }

        if(request.getJobGeoType() != null && !request.getJobGeoType().equals(JobGeoType.ALL)) {
            query.addCriteria(Criteria.where("geoType").is(request.getJobGeoType()));
        }

        // add location filter
        addLocationFilter(query, request.getLat(), request.getLon(),
                request.getDistance(), request.getJobGeoType());

        log.debug("Admin search query: {}", query);

        return query;
    }

    private JobFacetResult getJobPublicFacets(CriteriaDefinition criteriaDefinition) {
        FacetOperation facetOperation = facet(
                project("label"),
                unwind("label"),
                group("label").count().as("n"),
                project("n").and("label").previousOperation(),
                sort(DESC, "n")
        ).as("label") //label facet

                .and(
                        project("city"),
                        group("city").count().as("n"),
                        project("n").and("city").previousOperation(),
                        sort(DESC, "n")
                ).as("city") //city facet

                .and(
                        count().as("count")
                ).as("cnt"); //all count

        TypedAggregation agg = newAggregation(Job.class,
                match(criteriaDefinition),
                facetOperation
        );

        AggregationResults<JobFacetResult> results = mongoTemplate.aggregate(agg, JobFacetResult.class);

        return results.getMappedResults().get(0);
    }

    private JobAdminFacetResult getJobAdminFacets(CriteriaDefinition criteriaDefinition) {
        FacetOperation facetOperation = facet(
                project("label"),
                unwind("label"),
                group("label").count().as("n"),
                project("n").and("label").previousOperation(),
                sort(DESC, "n")
        ).as("label") //label facet
                .and(
                        project("city"),
                        group("city").count().as("n"),
                        project("n").and("city").previousOperation(),
                        sort(DESC, "n")
                ).as("city") //city facet
                .and(
                        project("geoType"),
                        group("geoType").count().as("n"),
                        project("n").and("geoType").previousOperation(),
                        sort(DESC, "n")
                ).as("type") //type facet
                .and(
                        project("createdBy"),
                        group("createdBy").count().as("n"),
                        project("n").and("createdBy").previousOperation(),
                        sort(DESC, "n")
                ).as("user") //username facet
                .and(
                        project("status"),
                        group("status").count().as("n"),
                        project("n").and("status").previousOperation(),
                        sort(DESC, "n")
                ).as("status") //status facet
                .and(
                        count().as("count")
                ).as("cnt"); //all count

        TypedAggregation agg = newAggregation(Job.class,
                match(criteriaDefinition),
                facetOperation
        );

        AggregationResults<JobAdminFacetResult> results = mongoTemplate.aggregate(agg, JobAdminFacetResult.class);

        return results.getMappedResults().get(0);
    }

    private void addLocationFilter(Query query, Double lat, Double lon, Double distance, JobGeoType jobGeoType) {
        if (Utils.isValidLocationArray(lat, lon)) {
            Point point = new Point(lat, lon);

            Criteria withinLocation = Criteria
                    .where("geoPoint")
                    .withinSphere(new Circle(point, new Distance(
                            distance,
                            Metrics.KILOMETERS
                    )));

            switch (jobGeoType) {
                case ON_SITE:
                    query.addCriteria(withinLocation);
                    break;

                case ALL:
                    Criteria isRemote = Criteria.where("geoType").is(JobGeoType.REMOTE);
                    query.addCriteria(new Criteria().orOperator(isRemote, withinLocation));
                    break;

                default: // nothing to do
            }
        }
    }

    private void updateJobStatus(Job job, JobStatus status, boolean isAdmin) throws JobException {
        if(isAdmin || JobStatus.DISABLED.equals(status) || JobStatus.ARCHIVED.equals(status)) {
            job.setStatus(status);
        } else {
            throw new JobException(String.format("User cannot set %s status to the job.", status.name()));
        }
    }
}
