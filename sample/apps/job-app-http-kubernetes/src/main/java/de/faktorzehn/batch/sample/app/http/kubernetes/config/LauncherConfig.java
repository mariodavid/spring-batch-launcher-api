package de.faktorzehn.batch.sample.app.http.kubernetes.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import de.faktorzehn.batch.core.JobLauncherService;
import de.faktorzehn.batch.launcher.kubernetes.KubernetesJobLauncherService;
import de.faktorzehn.batch.launcher.kubernetes.config.KubernetesJobConfigurationResolver;

import io.fabric8.kubernetes.client.KubernetesClient;

@Configuration
public class LauncherConfig {

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
    public JobLauncherService jobLauncherService(KubernetesClient kubernetesClient, KubernetesJobConfigurationResolver kubernetesJobConfigurationResolver) {
        return new KubernetesJobLauncherService(kubernetesClient, kubernetesJobConfigurationResolver);
    }

}