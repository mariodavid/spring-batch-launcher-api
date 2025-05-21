package com.company.batch.launcher.local;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.NoSuchJobException;

import com.company.batch.core.JobExternalMappingUpdater;
import com.company.batch.core.TaskLauncher;
import com.company.batch.execution.BatchExecutor;


public class InMemoryTaskLauncher implements TaskLauncher {

    private final JobExternalMappingUpdater jobExternalMappingUpdater;
    private final BatchExecutor batchExecutor;

    public InMemoryTaskLauncher(JobExternalMappingUpdater jobExternalMappingUpdater, BatchExecutor batchExecutor) {
        this.jobExternalMappingUpdater = jobExternalMappingUpdater;
        this.batchExecutor = batchExecutor;
    }

    @Override
    public void launchTask(String externalJobExecutionId, String jobName, Map<String, Object> parameters) {

        try {
            JobExecution jobExecution = batchExecutor.execute(externalJobExecutionId, jobName, parameters);
            jobExternalMappingUpdater.updateExternalJobExecutionId(externalJobExecutionId, jobExecution.getId());
        }catch (Exception e) {
            throw new RuntimeException("Job-Start failed", e);
        }
    }

}