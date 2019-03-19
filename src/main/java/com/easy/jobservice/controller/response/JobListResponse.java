package com.easy.jobservice.controller.response;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class JobListResponse<T> implements Serializable {

    private List<T> jobs;
    private long totalElements = 0;
    private int totalPages = 0;

}
