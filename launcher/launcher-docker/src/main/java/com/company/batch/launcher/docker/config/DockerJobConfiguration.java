package com.company.batch.launcher.docker.config;

public record DockerJobConfiguration(
        String jobName,
        String dockerImageName,
        String dockerNetworkName,
        String springApplicationJson
) { }