package com.company.batch.launcher.http.config;

public interface HttpJobConfigurationResolver {

    HttpJobConfiguration resolve(String jobName);
}