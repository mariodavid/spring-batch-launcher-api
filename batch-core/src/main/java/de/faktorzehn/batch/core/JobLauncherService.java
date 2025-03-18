package de.faktorzehn.batch.core;

import java.util.Map;

public interface JobLauncherService {
    Long launchJob(String jobName, Map<String, Object> parameters);
}