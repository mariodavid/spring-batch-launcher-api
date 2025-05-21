package com.company.batch.sample.app.http.greeting.config;

import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.company.batch.core.JobExternalMappingUpdater;
import com.company.batch.core.TaskLauncher;
import com.company.batch.execution.BatchExecutor;
import com.company.batch.launcher.local.InMemoryTaskLauncher;

@Configuration
public class InMemoryLauncherConfig {

    @Bean
    public TaskLauncher taskLauncher(JobExternalMappingUpdater jobExternalMappingUpdater, BatchExecutor batchExecutor) {
        return new InMemoryTaskLauncher(jobExternalMappingUpdater, batchExecutor);
    }

}