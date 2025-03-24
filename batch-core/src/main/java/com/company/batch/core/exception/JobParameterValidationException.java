package com.company.batch.core.exception;


public class JobParameterValidationException extends RuntimeException {

    public JobParameterValidationException(String message) {
        super(message);
    }
    public JobParameterValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
