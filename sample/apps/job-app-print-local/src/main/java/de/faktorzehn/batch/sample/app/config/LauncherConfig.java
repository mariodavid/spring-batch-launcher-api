package de.faktorzehn.batch.sample.app.config;

import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.support.DefaultBatchConfiguration;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.TaskExecutorJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

import de.faktorzehn.batch.core.JobLauncherService;
import de.faktorzehn.batch.launcher.local.LocalJobLauncherService;

@Configuration
public class LauncherConfig {


    @Bean
    public JobLauncherService jobLauncherService(JobLauncher jobLauncher, JobRegistry jobRegistry) {
        return new LocalJobLauncherService(jobLauncher, jobRegistry);
    }

}