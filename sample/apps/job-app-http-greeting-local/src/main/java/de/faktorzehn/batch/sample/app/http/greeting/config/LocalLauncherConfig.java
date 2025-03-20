package de.faktorzehn.batch.sample.app.http.greeting.config;

import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import de.faktorzehn.batch.core.JobExternalMappingUpdater;
import de.faktorzehn.batch.core.JobLauncherService;
import de.faktorzehn.batch.launcher.local.LocalJobLauncherService;

@Configuration
public class LocalLauncherConfig {


    @Bean
    public JobLauncherService jobLauncherService(JobLauncher jobLauncher, JobRegistry jobRegistry, JobExternalMappingUpdater jobExternalMappingUpdater) {
        return new LocalJobLauncherService(jobLauncher, jobRegistry, jobExternalMappingUpdater);
    }

}