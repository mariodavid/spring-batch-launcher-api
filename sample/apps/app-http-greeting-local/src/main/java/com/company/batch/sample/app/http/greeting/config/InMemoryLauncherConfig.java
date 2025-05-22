package com.company.batch.sample.app.http.greeting.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.company.batch.core.JobExternalMappingUpdater;
import com.company.batch.core.TaskLauncher;
import com.company.batch.execution.BatchExecutor;
import com.company.batch.execution.JobExecutionStarter;
import com.company.batch.launcher.local.InMemoryTaskLauncher;
import com.company.batch.persistence.AcceptedJobRepository;
import com.company.batch.persistence.PersistenceJobExternalMappingUpdater;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class InMemoryLauncherConfig {

    @Bean
    public TaskLauncher taskLauncher(JobExecutionStarter jobExecutionStarter) {
        return new InMemoryTaskLauncher(jobExecutionStarter);
    }

    @Bean
    public JobExecutionStarter jobExecutionStarter(
            BatchExecutor batchExecutor,
            AcceptedJobRepository acceptedJobRepository,
            ObjectMapper objectMapper,
            JobExternalMappingUpdater jobExternalMappingUpdater
    ) {
        return new JobExecutionStarter(batchExecutor, acceptedJobRepository, objectMapper, jobExternalMappingUpdater);
    }

    @Bean
    public JobExternalMappingUpdater jobExternalMappingUpdater(AcceptedJobRepository acceptedJobRepository) {
        return new PersistenceJobExternalMappingUpdater(acceptedJobRepository);
    }
}