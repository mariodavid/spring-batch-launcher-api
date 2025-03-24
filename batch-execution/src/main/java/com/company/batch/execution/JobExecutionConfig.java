package com.company.batch.execution;

import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.company.batch.core.JobLauncherService;
import com.company.batch.core.exception.JobNotFoundException;
import com.company.batch.persistence.AcceptedJob;
import com.company.batch.persistence.AcceptedJobRepository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@EnableConfigurationProperties({
        JobProperties.class
})
@Configuration
public class JobExecutionConfig {

    private static final Logger log = LoggerFactory.getLogger(JobExecutionConfig.class);

    @Bean
    public CommandLineRunner executeJob(
            JobLauncherService jobLauncherService,
            JobProperties jobProperties,
            AcceptedJobRepository acceptedJobRepository,
            ObjectMapper objectMapper
    ) {
        String externalJobExecutionId = jobProperties.externalJobExecutionId();
        Optional<AcceptedJob> potentialAcceptedJob = acceptedJobRepository.findByExternalJobExecutionId(externalJobExecutionId);

        if (potentialAcceptedJob.isEmpty()) {
            log.error("Provided external job execution id {} does not exist", externalJobExecutionId);
            throw new JobNotFoundException("Provided external job execution id '%s' does not exist".formatted(externalJobExecutionId));
        }

        AcceptedJob acceptedJob = potentialAcceptedJob.get();
        try {
            Map<String, Object> jobParametersMap = objectMapper.readValue(acceptedJob.jobParameters(), new TypeReference<>() {});

            return args -> jobLauncherService.launchJob(
                    externalJobExecutionId,
                    acceptedJob.jobName(),
                    jobParametersMap
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
