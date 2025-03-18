package de.faktorzehn.batch.sample.app.shell.greeting;

import org.springframework.batch.core.Job;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import de.faktorzehn.batch.core.JobLauncherService;
import de.faktorzehn.batch.sample.app.shell.greeting.config.GreetingJobProperties;

@SpringBootApplication(scanBasePackages = {
        "de.faktorzehn.batch.sample.app.shell.greeting",
        "de.faktorzehn.batch.sample.job.greeting",
})
@EnableConfigurationProperties(GreetingJobProperties.class)
public class JobAppShellGreetingLocalApplication {

    public static void main(String[] args) {
        SpringApplication.run(JobAppShellGreetingLocalApplication.class, args);
    }


    @Bean
    public CommandLineRunner run(JobLauncherService jobLauncherService, GreetingJobProperties greetingJobProperties, Job greetingJob) {
        return args -> {
            jobLauncherService.launchJob(greetingJob.getName(), greetingJobProperties.toPrintJobParameters().getParameters());
        };
    }
}
