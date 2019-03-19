package com.easy.jobservice.entity;

import com.easy.jobservice.common.BaseEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Builder
@Document(collection = "job_photo")
public class JobPhoto extends BaseEntity<String> {

    @Id
    private String id;

    private String url;

    private String thumbnail;

}
