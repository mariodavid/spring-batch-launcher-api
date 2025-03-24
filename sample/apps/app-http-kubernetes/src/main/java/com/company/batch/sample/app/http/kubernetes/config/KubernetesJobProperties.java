package com.company.batch.sample.app.http.kubernetes.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.company.batch.launcher.kubernetes.config.KubernetesJobConfiguration;

@ConfigurationProperties(prefix = "job.kubernetes")
public record KubernetesJobProperties(List<KubernetesJobConfiguration> configurations) {

}