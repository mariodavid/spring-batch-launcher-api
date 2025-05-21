package com.company.batch.execution;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.boot.autoconfigure.batch.BatchProperties;
import org.springframework.stereotype.Component;

import com.company.batch.core.JobExternalMappingUpdater;
import com.company.batch.core.exception.JobParameterValidationException;

public class SpringBatchExecutor implements BatchExecutor {


    private static final Logger log = LoggerFactory.getLogger(SpringBatchExecutor.class);
    private final JobLauncher asyncJobLauncher;
    private final JobRegistry jobRegistry;
    private final JobExternalMappingUpdater jobExternalMappingUpdater;

    public SpringBatchExecutor(JobLauncher jobLauncher, JobRegistry jobRegistry, JobExternalMappingUpdater jobExternalMappingUpdater) {
        this.asyncJobLauncher = jobLauncher;
        this.jobRegistry = jobRegistry;
        this.jobExternalMappingUpdater = jobExternalMappingUpdater;
    }

    @Override
    public JobExecution execute(String externalJobExecutionId, String jobName, Map<String, Object> parameters) {

        try {
            Job job = findJob(jobName);
            JobExecution jobExecution = asyncJobLauncher.run(job, JobParametersFactory.convertToJobParameters(parameters));
            jobExternalMappingUpdater.updateExternalJobExecutionId(externalJobExecutionId, jobExecution.getId());
            return jobExecution;
        } catch (JobParametersInvalidException e) {
            log.info("Invalid job parameters: {}", parameters, e);
            throw new JobParameterValidationException("Invalid job parameters: " + parameters, e);
        } catch (Exception e) {
            throw new RuntimeException("Job-Start failed", e);
        }
    }


    private Job findJob(String jobName) throws NoSuchJobException {
        return jobRegistry.getJob(jobName);
    }
}