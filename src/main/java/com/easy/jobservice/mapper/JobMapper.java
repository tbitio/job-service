package com.easy.jobservice.mapper;

import com.easy.jobservice.dto.LocationDto;
import com.easy.jobservice.dto.PhotoDto;
import com.easy.jobservice.dto.JobDto;
import com.easy.jobservice.entity.Job;
import com.easy.jobservice.entity.JobPhoto;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.stream.Collectors;

@Component
public class JobMapper {

    public JobDto toDto(Job job) {
        LocationDto locationDto = new LocationDto();

        locationDto.setCity(job.getCity());
        locationDto.setCountry(job.getCountry());
        locationDto.setPlaceId(job.getPlaceId());
        locationDto.setPlaceName(job.getPlaceName());

        if(job.getGeoPoint() != null) {
            locationDto.setLat(job.getGeoPoint().getX());
            locationDto.setLon(job.getGeoPoint().getY());
        }

        return JobDto.builder()
                .id(job.getId())
                .location(locationDto)
                .name(job.getName())
                .telegramGroupUrl(job.getTelegramGroupUrl())
                .price(job.getPrice())
                .description(job.getDescription())
                .label(job.getLabel())
                .photos(job.getPhotos() != null ? job.getPhotos().stream()
                        .map(photo ->
                                PhotoDto.builder()
                                        .url(photo.getUrl())
                                        .thumbnail(photo.getThumbnail())
                                        .build()
                        ).collect(Collectors.toList()) :
                        null
                )
                .deadline(job.getDeadline() != null ? job.getDeadline().toString() : null)
                .type(job.getType())
                .status(job.getStatus())
                .geoType(job.getGeoType())
                .ownerName(job.getOwnerName())
                .createdBy(job.getCreatedBy())
                .build();
    }

    public Job toEntity(Job job, JobDto jobDto) {
        job.setName(jobDto.getName());

        job.setCity(jobDto.getLocation() != null ? jobDto.getLocation().getCity() : null);
        job.setCountry(jobDto.getLocation() != null ?  jobDto.getLocation().getCountry() : null);
        job.setLocation(
                jobDto.getLocation() != null ? jobDto.getLocation().getLat() : null,
                jobDto.getLocation() != null ? jobDto.getLocation().getLon() : null
        );
        job.setPlaceId(jobDto.getLocation() != null ?  jobDto.getLocation().getPlaceId() : null);
        job.setPlaceName(jobDto.getLocation() != null ?  jobDto.getLocation().getPlaceName() : null);

        if(jobDto.getPhotos() != null) {
            job.setPhotos(jobDto.getPhotos().stream()
                    .map(photoDto ->
                            JobPhoto.builder()
                                .url(photoDto.getUrl())
                                .thumbnail(photoDto.getThumbnail())
                                .build()
                    ).collect(Collectors.toList())
            );
        }

        job.setTelegramGroupUrl(jobDto.getTelegramGroupUrl());
        job.setPrice(jobDto.getPrice());

        job.setDescription(jobDto.getDescription());
        job.setLabel(jobDto.getLabel());

        if(jobDto.getDeadline() != null) {
            job.setDeadline(Instant.parse(jobDto.getDeadline()));
        }

        job.setType(jobDto.getType());
        job.setGeoType(jobDto.getGeoType());

        return job;
    }
}
