package com.company.batch.launcher.local;

import java.util.Map;

import com.company.batch.core.TaskLauncher;
import com.company.batch.execution.JobExecutionStarter;


public class InMemoryTaskLauncher implements TaskLauncher {

    private final JobExecutionStarter jobExecutionStarter;

    public InMemoryTaskLauncher(JobExecutionStarter jobExecutionStarter) {
        this.jobExecutionStarter = jobExecutionStarter;
    }

    @Override
    public void launchTask(String externalJobExecutionId, String jobName, Map<String, Object> parameters) {
        try {
            jobExecutionStarter.start(externalJobExecutionId);
        }catch (Exception e) {
            throw new RuntimeException("Job-Start failed", e);
        }
    }

}