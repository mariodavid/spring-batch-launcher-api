package de.faktorzehn.batch.launcher.local;

import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.NoSuchJobException;

import de.faktorzehn.batch.core.JobExternalMappingUpdater;
import de.faktorzehn.batch.core.JobLauncherService;
import de.faktorzehn.batch.core.exception.JobParameterValidationException;


public class LocalJobLauncherService implements JobLauncherService {

    private static final Logger log = LoggerFactory.getLogger(LocalJobLauncherService.class);
    private final JobLauncher asyncJobLauncher;
    private final JobRegistry jobRegistry;
    private final JobExternalMappingUpdater jobExternalMappingUpdater;

    public LocalJobLauncherService(JobLauncher jobLauncher, JobRegistry jobRegistry, JobExternalMappingUpdater jobExternalMappingUpdater) {
        this.asyncJobLauncher = jobLauncher;
        this.jobRegistry = jobRegistry;
        this.jobExternalMappingUpdater = jobExternalMappingUpdater;
    }

    @Override
    public void launchJob(String externalJobExecutionId, String jobName, Map<String, Object> parameters) {

        try {
            Job job = findJob(jobName);
            JobExecution jobExecution = asyncJobLauncher.run(job, JobParametersFactory.convertToJobParameters(parameters));
            jobExternalMappingUpdater.updateExternalJobExecutionId(externalJobExecutionId, jobExecution.getId());
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

}