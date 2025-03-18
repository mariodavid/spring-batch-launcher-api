package de.faktorzehn.batch.extapi;

public class JobResponse {
    private String errorMessage;
    private String executionId;

    public JobResponse(String executionId, String errorMessage) {
        this.executionId = executionId;
        this.errorMessage = errorMessage;
    }
    
    public String getExecutionId() {
        return executionId;
    }
    
    public void setExecutionId(String executionId) {
        this.executionId = executionId;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}