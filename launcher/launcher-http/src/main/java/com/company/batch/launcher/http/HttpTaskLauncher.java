package com.company.batch.launcher.http;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.client.RestClient;

import com.company.batch.core.TaskLauncher;
import com.company.batch.core.exception.JobExecutionCreationFailedException;
import com.company.batch.core.exception.JobNotFoundException;
import com.company.batch.core.exception.JobParameterValidationException;
import com.company.batch.extapi.JobRequest;
import com.company.batch.extapi.JobResponse;
import com.company.batch.launcher.http.config.HttpJobConfiguration;
import com.company.batch.launcher.http.config.HttpJobConfigurationResolver;

public class HttpTaskLauncher implements TaskLauncher {

    private static final Log log = LogFactory.getLog(HttpTaskLauncher.class);

    private final RestClient.Builder restClientBuilder;
    private final HttpJobConfigurationResolver httpJobConfigurationResolver;

    public HttpTaskLauncher(RestClient.Builder restClientBuilder, HttpJobConfigurationResolver httpJobConfigurationResolver) {
        this.restClientBuilder = restClientBuilder;
        this.httpJobConfigurationResolver = httpJobConfigurationResolver;
    }

    @Override
    public void launchTask(String externalJobExecutionId, String jobName, Map<String, Object> parameters) {
        HttpJobConfiguration httpJobConfiguration = httpJobConfigurationResolver.resolve(jobName);
        var restClient = restClientBuilder
                .baseUrl(httpJobConfiguration.baseUrl())
                .build();
        try {
            JobResponse jobResponse = restClient.post()
                    .uri("/jobs")
                    .body(new JobRequest(jobName, parameters, externalJobExecutionId))
                    .retrieve()
                    .body(JobResponse.class);

            if (jobResponse == null) {
                log.error("Job execution with id " + externalJobExecutionId + " could not be created");
                throw new JobExecutionCreationFailedException("Could not create job");
            }
            if (jobResponse.getErrorMessage() != null && !jobResponse.getErrorMessage().isEmpty()) {
                log.error("Job execution with id " + externalJobExecutionId + " failed: " + jobResponse.getErrorMessage());
                throw new JobParameterValidationException(jobResponse.getErrorMessage());
            }

            log.info("Job launched successfully via HTTP");
        } catch (org.springframework.web.client.HttpClientErrorException e) {
            if (e.getStatusCode() == org.springframework.http.HttpStatus.BAD_REQUEST) {
                JobResponse responseBody = e.getResponseBodyAs(JobResponse.class);
                log.error("Job execution with id %s failed with Status %s: %s".formatted(externalJobExecutionId, e.getStatusCode(), responseBody.getErrorMessage()));
                throw new JobParameterValidationException(responseBody.getErrorMessage(), e);
            } else if (e.getStatusCode() == org.springframework.http.HttpStatus.NOT_FOUND) {
                log.error("Job execution with id %s failed with Status %s".formatted(externalJobExecutionId, e.getStatusCode()));
                throw new JobNotFoundException(e.getResponseBodyAsString(), e);
            } else {
                throw new RuntimeException("Job-Start failed", e);
            }
        } catch (Exception e) {
            throw new RuntimeException("Job-Start failed", e);
        }
    }
}