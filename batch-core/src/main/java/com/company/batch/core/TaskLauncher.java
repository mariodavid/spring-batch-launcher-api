package com.company.batch.core;

import java.util.Map;

public interface TaskLauncher {
    void launchTask(String externalJobExecutionId, String jobName, Map<String, Object> parameters);
}