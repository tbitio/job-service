package com.easy.jobservice.service;

import java.util.List;
import java.util.Set;

/**
 * The {@code JobService} interface provides methods in order to manage job labels.
 */
public interface JobLabelService {

    Set<String> find(String name);

    void save(List<String> labels);

    void onJobDelete(List<String> labels);

}
