package de.faktorzehn.batchapi.controller;

import java.util.stream.Collectors;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.faktorzehn.batchapi.service.BatchJobService;

@RestController
@RequestMapping("/jobs")
public class JobController {

    private final BatchJobService batchJobService;

    public JobController(BatchJobService batchJobService) {
        this.batchJobService = batchJobService;
    }

    @PostMapping
    public ResponseEntity<JobResponse> launchJob(@RequestBody JobRequest request) {
        try {
            Long executionId = batchJobService.launchJob(request);
            return ResponseEntity.ok(new JobResponse(executionId.toString()));
        } catch (NoSuchJobException e) {
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
        return ResponseEntity.ok(new JobResponse(restartedJobExecutionId.toString()));
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