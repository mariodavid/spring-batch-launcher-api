package com.company.batch.extapi;

import java.util.Map;

public record JobRequest(
        String jobName,
        Map<String, Object> jobParameters,
        String externalJobExecutionId
){}