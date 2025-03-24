package com.company.batch.extapi;

public class JobResponse {
    private String errorMessage;
    private String externalJobExecutionId;

    public JobResponse(String externalJobExecutionId, String errorMessage) {
        this.externalJobExecutionId = externalJobExecutionId;
        this.errorMessage = errorMessage;
    }
    
    public String getExternalJobExecutionId() {
        return externalJobExecutionId;
    }
    
    public void setExternalJobExecutionId(String externalJobExecutionId) {
        this.externalJobExecutionId = externalJobExecutionId;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}