package com.company.batch.sample.app.http.kubernetes.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.company.batch.core.TaskLauncher;
import com.company.batch.launcher.kubernetes.KubernetesTaskLauncher;
import com.company.batch.launcher.kubernetes.config.KubernetesJobConfigurationResolver;

import io.fabric8.kubernetes.client.KubernetesClient;

@Configuration
public class KubernetesLauncherConfig {

    @Bean
    public KubernetesJobConfigurationResolver kubernetesJobConfigurationResolver(KubernetesJobProperties kubernetesJobProperties) {
        return jobName -> kubernetesJobProperties.configurations()
                .stream()
                .filter(it -> it.jobName().equals(jobName))
                .findFirst()
                .orElseThrow()
                .jobTemplateName();
    }

    @Bean
    public TaskLauncher jobLauncherService(KubernetesClient kubernetesClient, KubernetesJobConfigurationResolver kubernetesJobConfigurationResolver) {
        return new KubernetesTaskLauncher(kubernetesClient, kubernetesJobConfigurationResolver);
    }

}