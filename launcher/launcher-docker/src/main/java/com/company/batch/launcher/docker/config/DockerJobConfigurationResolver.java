package com.company.batch.launcher.docker.config;

public interface DockerJobConfigurationResolver {

    DockerJobConfiguration resolve(String jobName);
}