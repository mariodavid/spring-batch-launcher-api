package com.company.batch.sample.app.http.kubernetes.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "job.kubernetes")
public record KubernetesJobProperties(List<KubernetesJobConfiguration> configurations) {

    public record KubernetesJobConfiguration(String jobName, String jobTemplateName) {
    }
}