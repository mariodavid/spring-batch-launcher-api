package com.company.batch.core;

public interface JobExternalMappingUpdater {

    void updateExternalJobExecutionId(String externalJobExecutionId, Long jobExecutionId);
}
