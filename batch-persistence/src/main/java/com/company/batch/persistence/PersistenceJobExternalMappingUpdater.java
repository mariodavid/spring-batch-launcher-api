package com.company.batch.persistence;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.company.batch.core.JobExternalMappingUpdater;
import com.company.batch.core.exception.JobNotFoundException;

public class PersistenceJobExternalMappingUpdater implements JobExternalMappingUpdater {

    private static final Logger log = LoggerFactory.getLogger(PersistenceJobExternalMappingUpdater.class);
    private final AcceptedJobRepository acceptedJobRepository;

    public PersistenceJobExternalMappingUpdater(AcceptedJobRepository acceptedJobRepository) {
        this.acceptedJobRepository = acceptedJobRepository;
    }

    @Override
    public void updateExternalJobExecutionId(String externalJobExecutionId, Long jobExecutionId) {
        log.info("Trying to update external job execution id {} with jobExecutionId: {}", externalJobExecutionId, jobExecutionId);
        Optional<AcceptedJob> potentialAcceptedJob = acceptedJobRepository.findByExternalJobExecutionId(externalJobExecutionId);

        if (potentialAcceptedJob.isEmpty()) {
            log.error("Provided external job execution id {} does not exist", externalJobExecutionId);
            throw new JobNotFoundException("Provided external job execution id %s does not exist".formatted(externalJobExecutionId));
        }

        AcceptedJob acceptedJob = potentialAcceptedJob.get();
        AcceptedJob updatedJob = new AcceptedJob(
                acceptedJob.externalJobExecutionId(),
                jobExecutionId,
                acceptedJob.version(),
                acceptedJob.sourceSystem(),
                acceptedJob.jobName(),
                acceptedJob.jobParameters(),
                acceptedJob.createdAt()
        );

        log.info("Updating accepted job {}", updatedJob);
        acceptedJobRepository.save(updatedJob);
    }
}
