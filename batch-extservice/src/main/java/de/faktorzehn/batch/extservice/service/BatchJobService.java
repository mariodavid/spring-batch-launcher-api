package de.faktorzehn.batch.extservice.service;

import java.time.LocalDate;
import java.util.Date;
import java.util.Map;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.stereotype.Service;

import de.faktorzehn.batch.extapi.JobRequest;


@Service
public class BatchJobService {
    
    private final JobLauncher asyncJobLauncher;
    private final JobExplorer jobExplorer;
    private final JobRegistry jobRegistry;
    private final JobOperator jobOperator;


    public BatchJobService(JobLauncher jobLauncher, JobExplorer jobExplorer, JobRegistry jobRegistry, JobOperator jobOperator) {
        this.asyncJobLauncher = jobLauncher;
        this.jobExplorer = jobExplorer;
        this.jobRegistry = jobRegistry;
        this.jobOperator = jobOperator;
    }

    public Long launchJob(JobRequest jobRequest) throws NoSuchJobException {

        Job job = findJob(jobRequest);

        try {
            JobExecution jobExecution = asyncJobLauncher.run(job, convertToJobParameters(jobRequest.jobParameters()));
            return jobExecution.getId();
        } catch (Exception e) {
            throw new RuntimeException("Job-Start failed", e);
        }
    }

    private Job findJob(JobRequest jobRequest) throws NoSuchJobException {
        return jobRegistry.getJob(jobRequest.jobName());
    }


    public Long restartJob(Long executionId)  {
        try {
            return jobOperator.restart(executionId);
        } catch (Exception e) {
            throw new RuntimeException("Job-Restart failed", e);
        }
    }

    public void stop(long executionId) {
        try {
            jobOperator.stop(executionId);
        }
        catch (Exception e) {
            throw new RuntimeException("Job-Abondon failed", e);
        }
    }

    public JobParameters convertToJobParameters(Map<String, Object> parameters) {
        JobParametersBuilder builder = new JobParametersBuilder();
        parameters.forEach((key, value) -> {
            switch (value) {
                case Number number -> builder.addLong(key, number.longValue());
                case Date date -> builder.addDate(key, date);
                case LocalDate localDate -> builder.addLocalDate(key, localDate);
                case null, default -> builder.addString(key, value != null ? value.toString() : "");
            }
        });
        builder.addLong("time", System.currentTimeMillis());
        return builder.toJobParameters();
    }

    public JobExecution getJobExecution(Long executionId) {
        return jobExplorer.getJobExecution(executionId);
    }

}