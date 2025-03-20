package de.faktorzehn.batch.sample.app.http.docker.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

import de.faktorzehn.batch.launcher.docker.config.DockerJobConfiguration;

@ConfigurationProperties(prefix = "job.docker")
public record DockerJobProperties(List<DockerJobConfiguration> configurations) {


}