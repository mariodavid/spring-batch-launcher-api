package com.company.batch.core;

import java.util.Map;
import java.util.UUID;

public interface JobLauncherService {
    void launchJob(String externalJobExecutionId, String jobName, Map<String, Object> parameters);
}