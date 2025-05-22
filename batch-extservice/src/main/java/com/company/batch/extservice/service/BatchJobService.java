package com.company.batch.extservice.service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.batch.core.TaskLauncher;
import com.company.batch.core.exception.JobExecutionCreationFailedException;
import com.company.batch.core.exception.JobNotFoundException;
import com.company.batch.core.exception.JobNotStartedException;
import com.company.batch.persistence.AcceptedJob;
import com.company.batch.persistence.AcceptedJobRepository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


@Service
public class BatchJobService {
    
    private final JobExplorer jobExplorer;
    private final JobOperator jobOperator;
    private final TaskLauncher jobLauncherService;
    private final AcceptedJobRepository externalJobExecutionMappingRepository;
    private final ObjectMapper objectMapper;
    private final AcceptedJobRepository acceptedJobRepository;


    public BatchJobService(JobExplorer jobExplorer, JobOperator jobOperator, TaskLauncher jobLauncherService, AcceptedJobRepository externalJobExecutionMappingRepository, ObjectMapper objectMapper, AcceptedJobRepository acceptedJobRepository) {
        this.jobExplorer = jobExplorer;
        this.jobOperator = jobOperator;
        this.jobLauncherService = jobLauncherService;
        this.externalJobExecutionMappingRepository = externalJobExecutionMappingRepository;
        this.objectMapper = objectMapper;
        this.acceptedJobRepository = acceptedJobRepository;
    }

    public Long restartJob(String externalJobExecutionId)  {
        try {
            return jobOperator.restart(lookupExecutionId(externalJobExecutionId));
        } catch (Exception e) {
            throw new RuntimeException("Job-Restart failed", e);
        }
    }

    public void stop(String executionId) {
        try {
            jobOperator.stop(lookupExecutionId(executionId));
        }
        catch (Exception e) {
            throw new RuntimeException("Job-Abondon failed", e);
        }
    }

    public JobExecution getJobExecution(String externalJobExecutionId) {
        return jobExplorer.getJobExecution(lookupExecutionId(externalJobExecutionId));
    }

    private Long lookupExecutionId(String externalJobExecutionId) {
        Optional<AcceptedJob> potentialAcceptedJob = acceptedJobRepository.findByExternalJobExecutionId(externalJobExecutionId);

        if (potentialAcceptedJob.isEmpty()) {
            throw new JobNotFoundException("Job not found for externalJobExecutionId: " + externalJobExecutionId);
        }
        AcceptedJob acceptedJob1 = potentialAcceptedJob.get();

        Long executionId = acceptedJob1.jobExecutionId();

        if (executionId == null) {
            throw new JobNotStartedException("Job found for externalJobExecutionId: %s. But not yet started (executionId is null).".formatted(externalJobExecutionId));
        }
        return executionId;
    }

    public JobLaunchResult launchJob(String jobName, Map<String, Object> jobParameters, String existingExternalJobExecutionId) {

        ensureJobWithParametersIsNotAlreadyPresent(jobName, jobParameters);

        AcceptedJob acceptedJob = acceptJob(
                jobName,
                jobParameters,
                existingExternalJobExecutionId
        );

        jobLauncherService.launchTask(acceptedJob.externalJobExecutionId(), jobName, jobParameters);

        return new JobLaunchResult(acceptedJob.externalJobExecutionId());
    }


    @Transactional
    public AcceptedJob acceptJob(String jobName, Map<String, Object> jobParameters, String existingExternalJobExecutionId) {
        ensureJobWithParametersIsNotAlreadyPresent(jobName, jobParameters);

        return findOrCreateExternalMapping(existingExternalJobExecutionId, jobName, jobParameters);
    }

    private void ensureJobWithParametersIsNotAlreadyPresent(String jobName, Map<String, Object> jobParameters) {
        JobInstance existingJobInstance = jobExplorer.getJobInstance(jobName, JobParametersFactory.convertToJobParameters(jobParameters));

        if (existingJobInstance != null) {
            throw new JobExecutionCreationFailedException("Job-Execution already exists: %s".formatted(existingJobInstance.getInstanceId()));
        }
    }

    private AcceptedJob findOrCreateExternalMapping(String existingExternalJobExecutionId, String jobName, Map<String, Object> jobParameters) {
        if (existingExternalJobExecutionId != null) {
            Optional<AcceptedJob> existingMapping = externalJobExecutionMappingRepository.findByExternalJobExecutionId(existingExternalJobExecutionId);

            if (existingMapping.isPresent()) {
                if (existingMapping.get().jobExecutionId() != null) {
                    throw new JobExecutionCreationFailedException("Job-Execution already exists: %s".formatted(existingMapping.get().jobExecutionId()));
                }
                return existingMapping.get();
            }

        }

        String externalJobExecutionId = UUID.randomUUID().toString();
        try {
            return externalJobExecutionMappingRepository.save(new AcceptedJob(
                    externalJobExecutionId,
                    null,
                    null,
                    "source-system",
                    jobName,
                    objectMapper.writeValueAsString(jobParameters),
                    LocalDateTime.now()));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}