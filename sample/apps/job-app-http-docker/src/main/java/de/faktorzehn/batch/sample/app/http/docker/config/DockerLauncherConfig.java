package de.faktorzehn.batch.sample.app.http.docker.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import de.faktorzehn.batch.core.JobLauncherService;
import de.faktorzehn.batch.launcher.docker.config.DockerJobConfigurationResolver;
import de.faktorzehn.batch.launcher.docker.DockerJobLauncherService;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.okhttp.OkDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;

@Configuration
public class DockerLauncherConfig {

    private static final Logger log = LoggerFactory.getLogger(DockerLauncherConfig.class);

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