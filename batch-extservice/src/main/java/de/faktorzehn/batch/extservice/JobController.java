package de.faktorzehn.batch.extservice;

import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.faktorzehn.batch.core.exception.JobExecutionCreationFailedException;
import de.faktorzehn.batch.core.exception.JobParameterValidationException;
import de.faktorzehn.batch.extapi.JobRequest;
import de.faktorzehn.batch.extapi.JobResponse;
import de.faktorzehn.batch.extapi.JobStatusResponse;
import de.faktorzehn.batch.extservice.service.BatchJobService;
import de.faktorzehn.batch.extservice.service.JobLaunchResult;


@RestController
@RequestMapping("/jobs")
public class JobController {

    private static final Logger log = LoggerFactory.getLogger(JobController.class);
    private final BatchJobService batchJobService;

    public JobController(BatchJobService batchJobService) {
        this.batchJobService = batchJobService;
    }

    @PostMapping
    public ResponseEntity<JobResponse> launchJob(@RequestBody JobRequest request) {
        try {
            JobLaunchResult jobLaunchResult = batchJobService.launchJob(request.jobName(), request.jobParameters(), request.externalJobExecutionId());
            return ResponseEntity.ok(new JobResponse(jobLaunchResult.externalJobExecutionId(), null));
        } catch (JobParameterValidationException e) {
            return ResponseEntity.badRequest()
                    .body(new JobResponse(null,e.getMessage()));
        }  catch (JobExecutionCreationFailedException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new JobResponse(null,e.getMessage()));
        } catch (Exception e) {
            log.error("Error while launching job: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(new JobResponse(null,e.getMessage()));
        }
    }

    @GetMapping("/{externalJobExecutionId}")
    public ResponseEntity<JobStatusResponse> getJobStatus(@PathVariable String externalJobExecutionId) {
        JobExecution jobExecution = batchJobService.getJobExecution(externalJobExecutionId);
        if (jobExecution == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(mapToResponse(externalJobExecutionId, jobExecution));
    }


    @PostMapping("/{externalJobExecutionId}/stop")
    public ResponseEntity<String> stopJob(@PathVariable String externalJobExecutionId) {
        batchJobService.stop(externalJobExecutionId);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/{externalJobExecutionId}/restart")
    public ResponseEntity<JobResponse> restartJob(@PathVariable  String externalJobExecutionId) {
        Long restartedJobExecutionId = batchJobService.restartJob(externalJobExecutionId);
        return ResponseEntity.ok(new JobResponse(restartedJobExecutionId.toString(), null));
    }

    private static JobStatusResponse mapToResponse(String externalJobExecutionId, JobExecution jobExecution) {
        return new JobStatusResponse(
                externalJobExecutionId,
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