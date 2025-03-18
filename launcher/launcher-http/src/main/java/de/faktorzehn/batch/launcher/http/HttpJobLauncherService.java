package de.faktorzehn.batch.launcher.http;

import java.util.Map;

import org.springframework.web.client.RestClient;

import de.faktorzehn.batch.core.JobLauncherService;
import de.faktorzehn.batch.extapi.JobRequest;
import de.faktorzehn.batch.extapi.JobResponse;
import de.faktorzehn.batch.launcher.http.config.HttpJobConfigurationResolver;

public class HttpJobLauncherService implements JobLauncherService {


    private final RestClient.Builder restClientBuilder;
    private final HttpJobConfigurationResolver httpJobConfigurationResolver;

    public HttpJobLauncherService(RestClient.Builder restClientBuilder, HttpJobConfigurationResolver httpJobConfigurationResolver) {
        this.restClientBuilder = restClientBuilder;
        this.httpJobConfigurationResolver = httpJobConfigurationResolver;
    }

    @Override
    public Long launchJob(String jobName, Map<String, Object> parameters) {
        String baseUrl = httpJobConfigurationResolver.resolveBaseUrl(jobName);
        var restClient = restClientBuilder
                .baseUrl(baseUrl)
                .build();
        try {
            JobResponse jobResponse = restClient.post()
                    .uri("/jobs")
                    .body(new JobRequest(jobName, parameters))
                    .retrieve()
                    .body(JobResponse.class);
            return Long.parseLong(jobResponse.getExecutionId());
        } catch (Exception e) {
            throw new RuntimeException("Job-Start failed", e);
        }
    }

}