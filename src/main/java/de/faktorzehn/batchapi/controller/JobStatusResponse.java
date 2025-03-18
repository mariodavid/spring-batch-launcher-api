package de.faktorzehn.batchapi.api;

public class JobStatusResponse {
    private String executionId;
    private String status;
    
    public JobStatusResponse(String executionId, String status) {
        this.executionId = executionId;
        this.status = status;
    }
    
    public String getExecutionId() {
        return executionId;
    }
    
    public void setExecutionId(String executionId) {
        this.executionId = executionId;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
}