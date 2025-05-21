package com.company.batch.execution;

import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;

import com.company.batch.core.TaskLauncher;
import com.company.batch.core.exception.JobNotFoundException;
import com.company.batch.persistence.AcceptedJob;
import com.company.batch.persistence.AcceptedJobRepository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;


public class JobExecutionStarter {

    private static final Logger log = LoggerFactory.getLogger(JobExecutionStarter.class);

    private final TaskLauncher taskLauncher;
    private final AcceptedJobRepository acceptedJobRepository;
    private final ObjectMapper objectMapper;

    public JobExecutionStarter(TaskLauncher taskLauncher, AcceptedJobRepository acceptedJobRepository, ObjectMapper objectMapper) {
        this.taskLauncher = taskLauncher;
        this.acceptedJobRepository = acceptedJobRepository;
        this.objectMapper = objectMapper;
    }

    public void start(String externalJobExecutionId) {
        Optional<AcceptedJob> potentialAcceptedJob = acceptedJobRepository.findByExternalJobExecutionId(externalJobExecutionId);

        if (potentialAcceptedJob.isEmpty()) {
            log.error("Provided external job execution id {} does not exist", externalJobExecutionId);
            throw new JobNotFoundException("Provided external job execution id '%s' does not exist".formatted(externalJobExecutionId));
        }

        AcceptedJob acceptedJob = potentialAcceptedJob.get();
        try {
            Map<String, Object> jobParametersMap = objectMapper.readValue(acceptedJob.jobParameters(), new TypeReference<>() {});

            taskLauncher.launchTask(
                    externalJobExecutionId,
                    acceptedJob.jobName(),
                    jobParametersMap
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
