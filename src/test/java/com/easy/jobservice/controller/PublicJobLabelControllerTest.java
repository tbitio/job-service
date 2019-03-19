package com.easy.jobservice.controller;

import com.easy.jobservice.exception.ObjectNotFoundException;
import com.easy.jobservice.service.JobLabelService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { MockServletContext.class })
@ComponentScan("com.easy.jobservice.service.impl")
public class PublicJobLabelControllerTest {

    private final String baseURL = "/api/v1/public/job-labels";

    private MockMvc mockMvc;
    private JobLabelService jobLabelService;

    @Before
    public void init() throws ObjectNotFoundException {
        jobLabelService = mock(JobLabelService.class);

        PublicJobLabelController controller = new PublicJobLabelController(jobLabelService);
        mockMvc = standaloneSetup(controller).build();
    }

    @Test
    public void findJobLabels() throws Exception {
        when(jobLabelService.find(anyString())).thenReturn(Collections.emptySet());
        mockMvc.perform(get(baseURL)).andExpect(status().isOk());
    }
}
