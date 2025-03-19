package de.faktorzehn.batch.extservice.service;

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

import de.faktorzehn.batch.core.JobLauncherService;
import de.faktorzehn.batch.core.exception.JobExecutionCreationFailedException;
import de.faktorzehn.batch.persistence.AcceptedJob;
import de.faktorzehn.batch.persistence.AcceptedJobRepository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


@Service
public class BatchJobService {
    
    private final JobExplorer jobExplorer;
    private final JobOperator jobOperator;
    private final JobLauncherService jobLauncherService;
    private final AcceptedJobRepository externalJobExecutionMappingRepository;
    private final ObjectMapper objectMapper;


    public BatchJobService(JobExplorer jobExplorer, JobOperator jobOperator, JobLauncherService jobLauncherService, AcceptedJobRepository externalJobExecutionMappingRepository, ObjectMapper objectMapper) {
        this.jobExplorer = jobExplorer;
        this.jobOperator = jobOperator;
        this.jobLauncherService = jobLauncherService;
        this.externalJobExecutionMappingRepository = externalJobExecutionMappingRepository;
        this.objectMapper = objectMapper;
    }

    public Long restartJob(Long executionId)  {
        try {
            return jobOperator.restart(executionId);
        } catch (Exception e) {
            throw new RuntimeException("Job-Restart failed", e);
        }
    }

    public void stop(long executionId) {
        try {
            jobOperator.stop(executionId);
        }
        catch (Exception e) {
            throw new RuntimeException("Job-Abondon failed", e);
        }
    }

    public JobExecution getJobExecution(Long executionId) {
        return jobExplorer.getJobExecution(executionId);
    }

    public JobLaunchResult launchJob(String jobName, Map<String, Object> jobParameters, String existingExternalJobExecutionId) {

        ensureJobWithParametersIsNotAlreadyPresent(jobName, jobParameters);

        AcceptedJob acceptedJob = acceptJob(
                jobName,
                jobParameters,
                existingExternalJobExecutionId
        );

        jobLauncherService.launchJob(acceptedJob.externalJobExecutionId(), jobName, jobParameters);

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