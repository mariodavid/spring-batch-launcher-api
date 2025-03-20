package de.faktorzehn.batch.launcher.docker.config;

public record DockerJobConfiguration(
        String jobName,
        String dockerImageName,
        String networkName,
        String springApplicationJson
) { }