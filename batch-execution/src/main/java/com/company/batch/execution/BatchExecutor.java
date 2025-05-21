package com.company.batch.execution;

import java.util.Map;

import org.springframework.batch.core.JobExecution;

public interface BatchExecutor {
    JobExecution execute(String externalJobExecutionId, String jobName, Map<String, Object> parameters);
}