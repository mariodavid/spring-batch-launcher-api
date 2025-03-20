package de.faktorzehn.batch.launcher.docker;

import java.util.Map;


import de.faktorzehn.batch.core.JobLauncherService;
import de.faktorzehn.batch.launcher.docker.config.DockerJobConfiguration;
import de.faktorzehn.batch.launcher.docker.config.DockerJobConfigurationResolver;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.HostConfig;

public class DockerJobLauncherService implements JobLauncherService {

    private final DockerClient dockerClient;
    private final DockerJobConfigurationResolver dockerJobConfigurationResolver;

    public DockerJobLauncherService(DockerClient dockerClient, DockerJobConfigurationResolver dockerJobConfigurationResolver) {
        this.dockerClient = dockerClient;
        this.dockerJobConfigurationResolver = dockerJobConfigurationResolver;
    }

    @Override
    public void launchJob(String externalJobExecutionId, String jobName, Map<String, Object> parameters) {
        parameters.put("externalJobExecutionId", externalJobExecutionId);

        DockerJobConfiguration dockerJobConfiguration = dockerJobConfigurationResolver.resolve(jobName);


        CreateContainerResponse container = dockerClient.createContainerCmd(dockerJobConfiguration.dockerImageName())
                .withEnv(
                        "SPRING_APPLICATION_JSON=" + dockerJobConfiguration.springApplicationJson(),
                        "JOB_EXTERNAL_JOB_EXECUTION_ID=" + externalJobExecutionId
                )
                .withLabels(
                        Map.of("de.faktorzehn.batch.job.external-job-execution-id", externalJobExecutionId)
                )
                .withHostConfig(
                        HostConfig.newHostConfig()
                                .withNetworkMode(dockerJobConfiguration.networkName())
                )
                .withName(jobName.toLowerCase() + "-" + System.currentTimeMillis())
                .exec();

        dockerClient.startContainerCmd(container.getId()).exec();
    }
}