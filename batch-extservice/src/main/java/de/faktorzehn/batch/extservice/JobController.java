package de.faktorzehn.batch.extservice;

import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.faktorzehn.batch.core.JobLauncherService;
import de.faktorzehn.batch.core.exception.JobParameterValidationException;
import de.faktorzehn.batch.extapi.JobRequest;
import de.faktorzehn.batch.extapi.JobResponse;
import de.faktorzehn.batch.extapi.JobStatusResponse;
import de.faktorzehn.batch.extservice.service.BatchJobService;


@RestController
@RequestMapping("/jobs")
public class JobController {

    private static final Logger log = LoggerFactory.getLogger(JobController.class);
    private final BatchJobService batchJobService;
    private final JobLauncherService jobLauncherService;

    public JobController(BatchJobService batchJobService, JobLauncherService jobLauncherService) {
        this.batchJobService = batchJobService;
        this.jobLauncherService = jobLauncherService;
    }

    @PostMapping
    public ResponseEntity<JobResponse> launchJob(@RequestBody JobRequest request) {
        try {
            Long executionId = jobLauncherService.launchJob(request.jobName(), request.jobParameters());
            return ResponseEntity.ok(new JobResponse(executionId.toString(), null));
        } catch (JobParameterValidationException e) {
            return ResponseEntity.badRequest()
                    .body(new JobResponse(null,e.getMessage()));
        } catch (Exception e) {
            log.error("Error while launching job: {}", e.getMessage(), e);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{executionId}")
    public ResponseEntity<JobStatusResponse> getJobStatus(@PathVariable Long executionId) {
        JobExecution jobExecution = batchJobService.getJobExecution(executionId);
        if (jobExecution == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(mapToResponse(executionId, jobExecution));
    }


    @PostMapping("/{executionId}/stop")
    public ResponseEntity<String> stopJob(@PathVariable long executionId) {
        batchJobService.stop(executionId);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/{executionId}/restart")
    public ResponseEntity<JobResponse> restartJob(@PathVariable Long executionId) {
        Long restartedJobExecutionId = batchJobService.restartJob(executionId);
        return ResponseEntity.ok(new JobResponse(restartedJobExecutionId.toString(), null));
    }

    private static JobStatusResponse mapToResponse(Long executionId, JobExecution jobExecution) {
        return new JobStatusResponse(
                executionId.toString(),
                jobExecution.getStatus().toString(),
                jobExecution.getStartTime(),
                jobExecution.getEndTime(),
                jobExecution.getExitStatus().getExitCode(),
                jobExecution.getAllFailureExceptions()
                        .stream()
                        .map(Throwable::getMessage)
                        .collect(Collectors.joining("; "))
        );
    }
}