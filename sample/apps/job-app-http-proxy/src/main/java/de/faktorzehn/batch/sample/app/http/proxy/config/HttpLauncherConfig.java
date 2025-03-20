package de.faktorzehn.batch.sample.app.http.proxy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

import de.faktorzehn.batch.core.JobLauncherService;
import de.faktorzehn.batch.launcher.http.HttpJobLauncherService;
import de.faktorzehn.batch.launcher.http.config.HttpJobConfigurationResolver;

@Configuration
public class HttpLauncherConfig {

    @Bean
    public HttpJobConfigurationResolver httpJobConfigurationResolver(HttpJobProperties httpJobProperties) {
        return jobName -> httpJobProperties.configurations()
                .stream()
                .filter(it -> it.jobName().equals(jobName))
                .findFirst()
                .orElseThrow();
    }

    @Bean
    public JobLauncherService jobLauncherService(RestClient.Builder restClientBuilder, HttpJobConfigurationResolver httpJobConfigurationResolver) {
        return new HttpJobLauncherService(restClientBuilder, httpJobConfigurationResolver);
    }

}