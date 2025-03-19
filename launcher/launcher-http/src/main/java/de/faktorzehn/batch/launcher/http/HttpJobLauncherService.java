package de.faktorzehn.batch.launcher.http;

import java.util.Map;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.client.RestClient;

import de.faktorzehn.batch.core.JobLauncherService;
import de.faktorzehn.batch.core.exception.JobExecutionCreationFailedException;
import de.faktorzehn.batch.core.exception.JobNotFoundException;
import de.faktorzehn.batch.core.exception.JobParameterValidationException;
import de.faktorzehn.batch.extapi.JobRequest;
import de.faktorzehn.batch.extapi.JobResponse;
import de.faktorzehn.batch.launcher.http.config.HttpJobConfigurationResolver;

public class HttpJobLauncherService implements JobLauncherService {


    private static final Log log = LogFactory.getLog(HttpJobLauncherService.class);
    private final RestClient.Builder restClientBuilder;
    private final HttpJobConfigurationResolver httpJobConfigurationResolver;

    public HttpJobLauncherService(RestClient.Builder restClientBuilder, HttpJobConfigurationResolver httpJobConfigurationResolver) {
        this.restClientBuilder = restClientBuilder;
        this.httpJobConfigurationResolver = httpJobConfigurationResolver;
    }

    @Override
    public void launchJob(String externalJobExecutionId, String jobName, Map<String, Object> parameters) {
        String baseUrl = httpJobConfigurationResolver.resolveBaseUrl(jobName);
        var restClient = restClientBuilder
                .baseUrl(baseUrl)
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