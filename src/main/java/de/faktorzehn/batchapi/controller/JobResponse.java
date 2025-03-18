package de.faktorzehn.batchapi.controller;

public class JobResponse {
    private String executionId;
    
    public JobResponse(String executionId) {
        this.executionId = executionId;
    }
    
    public String getExecutionId() {
        return executionId;
    }
    
    public void setExecutionId(String executionId) {
        this.executionId = executionId;
    }
}