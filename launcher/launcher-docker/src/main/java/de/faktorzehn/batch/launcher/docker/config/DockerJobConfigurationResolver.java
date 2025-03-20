package de.faktorzehn.batch.launcher.docker.config;

public interface DockerJobConfigurationResolver {

    DockerJobConfiguration resolve(String jobName);
}