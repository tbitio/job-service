package com.easy.jobservice.entity;

import com.easy.jobservice.common.BaseEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Builder
@Document(collection = "label")
public class JobLabel extends BaseEntity<String> {

    @Id
    private String id;

    @Indexed
    private String name;

    private int count = 0;

}
