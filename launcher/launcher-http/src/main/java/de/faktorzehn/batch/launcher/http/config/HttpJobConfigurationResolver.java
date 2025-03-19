package de.faktorzehn.batch.launcher.http.config;

public interface HttpJobConfigurationResolver {

    String resolveBaseUrl(String jobName);
}