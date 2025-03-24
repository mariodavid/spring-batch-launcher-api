package com.company.batch.sample.job.greeting;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersValidator;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class GreetingJobConfiguration {

    private static final Logger log = LoggerFactory.getLogger(GreetingJobConfiguration.class);
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    public GreetingJobConfiguration(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
    }

    @Bean
    public Job greetingJob(JobParametersValidator greetingJobParametersValidator) {
        return new JobBuilder("greetingJob", jobRepository)

                .validator(greetingJobParametersValidator)
                .start(
                        new StepBuilder("greetingStep", jobRepository)
                                .tasklet((contribution, chunkContext) -> {
                                    GreetingJobParameters printJobParameters = GreetingJobParameters.fromJobParameters(chunkContext.getStepContext()
                                            .getJobParameters());
                                    Long timeout = printJobParameters.timeout();
                                    String greetingName = printJobParameters.greetingName();
                                    log.info("Hello {}, sleeping for {} seconds", greetingName, timeout);
                                    Thread.sleep(timeout);
                                    return RepeatStatus.FINISHED;
                                }, transactionManager)
                                .build()
                )
                .build();
    }

    @Bean
    public JobParametersValidator greetingJobParametersValidator() {
        return GreetingJobParameters::fromJobParameters;
    }

}