package com.company.batch.launcher.docker;

import java.util.Map;


import com.company.batch.core.TaskLauncher;
import com.company.batch.launcher.docker.config.DockerJobConfiguration;
import com.company.batch.launcher.docker.config.DockerJobConfigurationResolver;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.HostConfig;

public class DockerTaskLauncher implements TaskLauncher {

    private final DockerClient dockerClient;
    private final DockerJobConfigurationResolver dockerJobConfigurationResolver;

    public DockerTaskLauncher(DockerClient dockerClient, DockerJobConfigurationResolver dockerJobConfigurationResolver) {
        this.dockerClient = dockerClient;
        this.dockerJobConfigurationResolver = dockerJobConfigurationResolver;
    }

    @Override
    public void launchTask(String externalJobExecutionId, String jobName, Map<String, Object> parameters) {

        DockerJobConfiguration dockerJobConfiguration = dockerJobConfigurationResolver.resolve(jobName);

        CreateContainerResponse container = dockerClient.createContainerCmd(dockerJobConfiguration.dockerImageName())
                .withEnv(
                        "SPRING_APPLICATION_JSON=" + dockerJobConfiguration.springApplicationJson(),
                        "JOB_EXTERNAL_JOB_EXECUTION_ID=" + externalJobExecutionId
                )
                .withLabels(
                        Map.of("com.company.batch.job.external-job-execution-id", externalJobExecutionId)
                )
                .withHostConfig(
                        HostConfig.newHostConfig()
                                .withNetworkMode(dockerJobConfiguration.dockerNetworkName())
                )
                .withName(jobName.toLowerCase() + "-" + externalJobExecutionId)
                .exec();

        dockerClient.startContainerCmd(container.getId()).exec();
    }
}