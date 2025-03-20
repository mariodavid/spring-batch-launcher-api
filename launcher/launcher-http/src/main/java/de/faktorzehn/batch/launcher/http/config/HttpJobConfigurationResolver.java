package de.faktorzehn.batch.launcher.http.config;

public interface HttpJobConfigurationResolver {

    HttpJobConfiguration resolve(String jobName);
}