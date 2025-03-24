package com.company.batch.sample.app.shell.greeting.config;

import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.company.batch.core.JobExternalMappingUpdater;
import com.company.batch.core.JobLauncherService;
import com.company.batch.launcher.local.LocalJobLauncherService;

@Configuration
public class LocalLauncherConfig {

    @Bean
    public JobLauncherService jobLauncherService(JobLauncher jobLauncher, JobRegistry jobRegistry, JobExternalMappingUpdater jobExternalMappingUpdater) {
        return new LocalJobLauncherService(jobLauncher, jobRegistry, jobExternalMappingUpdater);
    }
}