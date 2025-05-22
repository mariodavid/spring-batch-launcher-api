package com.company.batch.execution;

import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;

import com.company.batch.core.JobExternalMappingUpdater;
import com.company.batch.core.exception.JobNotFoundException;
import com.company.batch.persistence.AcceptedJob;
import com.company.batch.persistence.AcceptedJobRepository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;


public class JobExecutionStarter {

    private static final Logger log = LoggerFactory.getLogger(JobExecutionStarter.class);

    private final BatchExecutor batchExecutor;
    private final AcceptedJobRepository acceptedJobRepository;
    private final ObjectMapper objectMapper;
    private final JobExternalMappingUpdater jobExternalMappingUpdater;

    public JobExecutionStarter(BatchExecutor batchExecutor, AcceptedJobRepository acceptedJobRepository, ObjectMapper objectMapper, JobExternalMappingUpdater jobExternalMappingUpdater) {
        this.batchExecutor = batchExecutor;
        this.acceptedJobRepository = acceptedJobRepository;
        this.objectMapper = objectMapper;
        this.jobExternalMappingUpdater = jobExternalMappingUpdater;
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
            try {
                JobExecution jobExecution = batchExecutor.execute(externalJobExecutionId, acceptedJob.jobName(), jobParametersMap);
                jobExternalMappingUpdater.updateExternalJobExecutionId(externalJobExecutionId, jobExecution.getId());
            }catch (Exception e) {
                throw new RuntimeException("Job-Start failed", e);
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
