package com.company.batch.persistence;


import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;

@Table("accepted_job")
public record AcceptedJob(
        @Id String externalJobExecutionId,
        Long jobExecutionId,
        @Version Integer version,
        String sourceSystem,
        String jobName,
        String jobParameters,
        LocalDateTime createdAt
) {}