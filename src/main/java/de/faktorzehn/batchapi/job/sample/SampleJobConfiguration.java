package de.faktorzehn.batchapi.job.sample;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class SampleJobConfiguration {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    public SampleJobConfiguration(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
    }


    @Bean
    public Job sampleJob() {
        return new JobBuilder("sampleJob", jobRepository)
                .start(
                        new StepBuilder("sampleStep", jobRepository)
                                .tasklet((contribution, chunkContext) -> {
                                    Long timeout = (Long) chunkContext.getStepContext()
                                            .getJobParameters()
                                            .get("timeout");
                                    System.out.println("Starte Job mit Parameter: " + timeout);
                                    Thread.sleep(timeout);
                                    return RepeatStatus.FINISHED;
                                }, transactionManager)
                                .build()
                )
                .build();
    }
}