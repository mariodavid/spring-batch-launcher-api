package de.faktorzehn.batch.launcher.local;

import java.time.LocalDate;
import java.util.Date;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.NoSuchJobException;

import de.faktorzehn.batch.core.JobLauncherService;
import de.faktorzehn.batch.core.exception.JobParameterValidationException;


public class LocalJobLauncherService implements JobLauncherService {

    private static final Logger log = LoggerFactory.getLogger(LocalJobLauncherService.class);
    private final JobLauncher asyncJobLauncher;
    private final JobRegistry jobRegistry;

    public LocalJobLauncherService(JobLauncher jobLauncher, JobRegistry jobRegistry) {
        this.asyncJobLauncher = jobLauncher;
        this.jobRegistry = jobRegistry;
    }

    @Override
    public Long launchJob(String jobName, Map<String, Object> parameters) {

        try {
            Job job = findJob(jobName);
            JobExecution jobExecution = asyncJobLauncher.run(job, convertToJobParameters(parameters));
            return jobExecution.getId();
        } catch (JobParametersInvalidException e) {
            log.info("Invalid job parameters: {}", parameters, e);
            throw new JobParameterValidationException("Invalid job parameters: " + parameters, e);
        }catch (Exception e) {
            throw new RuntimeException("Job-Start failed", e);
        }
    }

    private Job findJob(String jobName) throws NoSuchJobException {
        return jobRegistry.getJob(jobName);
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

}