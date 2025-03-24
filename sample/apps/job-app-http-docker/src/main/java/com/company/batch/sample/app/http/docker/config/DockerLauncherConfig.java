package com.company.batch.sample.app.http.docker.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.company.batch.core.JobLauncherService;
import com.company.batch.launcher.docker.config.DockerJobConfigurationResolver;
import com.company.batch.launcher.docker.DockerJobLauncherService;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.okhttp.OkDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;

@Configuration
public class DockerLauncherConfig {

    @Bean
    public DockerJobConfigurationResolver dockerJobConfigurationResolver(DockerJobProperties dockerJobProperties) {
        return jobName -> dockerJobProperties.configurations()
                .stream()
                .filter(it -> it.jobName().equals(jobName))
                .findFirst()
                .orElseThrow();
    }

    @Bean
    public JobLauncherService jobLauncherService(DockerClient dockerClient, DockerJobConfigurationResolver dockerJobConfigurationResolver) {
        return new DockerJobLauncherService(dockerClient, dockerJobConfigurationResolver);
    }

    @Bean
    public DockerClient dockerClient() {
        DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .build();

        DockerHttpClient httpClient = new OkDockerHttpClient.Builder()
                .dockerHost(config.getDockerHost())
                .sslConfig(config.getSSLConfig())
                .build();

        return DockerClientBuilder.getInstance(config)
                .withDockerHttpClient(httpClient)
                .build();

    }

}