package com.company.batch.execution;

import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.company.batch.core.JobExternalMappingUpdater;
import com.company.batch.persistence.AcceptedJobRepository;
import com.company.batch.persistence.PersistenceJobExternalMappingUpdater;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
@ConditionalOnClass(JobExecutionStarter.class)
@EnableConfigurationProperties(JobProperties.class)
public class BatchCliExecutionAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public CommandLineRunner executeJob(
            JobExecutionStarter jobExecutionStarter,
            JobProperties jobProperties) {
        return args -> jobExecutionStarter.start(jobProperties.externalJobExecutionId());
    }

    @Bean
    @ConditionalOnMissingBean
    public JobExecutionStarter jobExecutionStarter(
            BatchExecutor batchExecutor,
            AcceptedJobRepository acceptedJobRepository,
            ObjectMapper objectMapper,
            JobExternalMappingUpdater jobExternalMappingUpdater
    ) {
        return new JobExecutionStarter(batchExecutor, acceptedJobRepository, objectMapper, jobExternalMappingUpdater);
    }

    @Bean
    @ConditionalOnMissingBean
    public JobExternalMappingUpdater jobExternalMappingUpdater(AcceptedJobRepository acceptedJobRepository) {
        return new PersistenceJobExternalMappingUpdater(acceptedJobRepository);
    }

    @Bean
    @ConditionalOnMissingBean
    public BatchExecutor batchExecutor(
            JobLauncher jobLauncher,
            JobRegistry jobRegistry,
            JobExternalMappingUpdater jobExternalMappingUpdater
    ) {
        return new SpringBatchExecutor(jobLauncher, jobRegistry, jobExternalMappingUpdater);
    }
}
