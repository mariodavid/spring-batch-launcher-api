package de.faktorzehn.batch.extapi;

import java.time.LocalDateTime;

public record JobStatusResponse(
        String externalJobExecutionId,
        String status,
        LocalDateTime startTime,
        LocalDateTime endTime,
        String exitStatus,
        String errorMessage
) {}