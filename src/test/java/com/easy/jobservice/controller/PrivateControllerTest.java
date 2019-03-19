package com.easy.jobservice.controller;

import com.easy.jobservice.dto.JobDto;
import com.easy.jobservice.exception.NoPermissionException;
import com.easy.jobservice.exception.ObjectNotFoundException;
import com.easy.jobservice.service.JobService;
import com.easy.jobservice.service.UploadService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.mock.web.MockServletContext;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.security.Principal;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { MockServletContext.class })
@ComponentScan("com.easy.jobservice.service.impl")
public class PrivateControllerTest {

    private final String jobID = "qwerty";
    private final String baseURL = "/api/v1/private/jobs";

    private MockMvc mockMvc;
    private JobService jobService;
    private UserDetailsService userDetailsService;
    private UploadService uploadService;
    private Principal principal;

    @Before
    public void init() throws ObjectNotFoundException {
        jobService = mock(JobService.class);
        userDetailsService = mock(UserDetailsService.class);
        uploadService = mock(UploadService.class);
        principal = mock(Principal.class);

        PrivateJobController controller = new PrivateJobController(jobService, userDetailsService, uploadService);
        mockMvc = standaloneSetup(controller).build();
    }

    @Test
    public void saveJobMissingRequiredFieldTest() throws Exception {
        mockMvc.perform(post(baseURL).content("{}").contentType("application/json"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void saveJobMissingRequestBodyTest() throws Exception {
        mockMvc.perform(post(baseURL))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateJobMissingRequiredFieldTest() throws Exception {
        mockMvc.perform(put(String.format("%s/%s", baseURL, jobID)).content("{}").contentType("application/json"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateJobMissingRequestBodyTest() throws Exception {
        mockMvc.perform(put(String.format("%s/%s", baseURL, jobID)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateMissingJobTest() throws Exception {
        when(principal.getName()).thenReturn("me");

        when(jobService.update(jobID, "me", JobDto.builder().name("test").build()))
                .thenThrow(ObjectNotFoundException.class);

        mockMvc.perform(put(String.format("%s/%s", baseURL, jobID))
                .principal(principal)
                .content("{\"name\": \"test\"}").contentType("application/json"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void updateNoPermissionTest() throws Exception {
        when(principal.getName()).thenReturn("me");

        when(jobService.update(jobID, "me", JobDto.builder().name("test").build()))
                .thenThrow(NoPermissionException.class);

        mockMvc.perform(put(String.format("%s/%s", baseURL, jobID))
                .principal(principal)
                .content("{\"name\": \"test\"}").contentType("application/json"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void deleteJobMissingJobTest() throws Exception {
        when(principal.getName()).thenReturn("me");
        doThrow(ObjectNotFoundException.class).when(jobService).delete(jobID, "me");

        mockMvc.perform(delete(String.format("%s/%s", baseURL, jobID)).principal(principal))
                .andExpect(status().isNotFound());
    }

    @Test
    public void deleteJobNoPermissionTest() throws Exception {
        when(principal.getName()).thenReturn("me");
        doThrow(NoPermissionException.class).when(jobService).delete(jobID, "me");

        mockMvc.perform(delete(String.format("%s/%s", baseURL, jobID)).principal(principal))
                .andExpect(status().isForbidden());
    }
}
