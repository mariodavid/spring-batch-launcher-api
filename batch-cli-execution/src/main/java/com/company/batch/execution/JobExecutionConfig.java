package com.company.batch.execution;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.company.batch.core.TaskLauncher;
import com.company.batch.persistence.AcceptedJobRepository;

import com.fasterxml.jackson.databind.ObjectMapper;

@EnableConfigurationProperties({
        JobProperties.class
})
@Configuration
public class JobExecutionConfig {

    @Bean
    public CommandLineRunner executeJob(
            JobExecutionStarter jobExecutionStarter,
            JobProperties jobProperties) {
        return args -> jobExecutionStarter.start(jobProperties.externalJobExecutionId());
    }

    @Bean
    public JobExecutionStarter jobExecutionStarter(
            TaskLauncher taskLauncher,
            AcceptedJobRepository acceptedJobRepository,
            ObjectMapper objectMapper
    ) {
        return new JobExecutionStarter(taskLauncher, acceptedJobRepository, objectMapper);
    }
}
