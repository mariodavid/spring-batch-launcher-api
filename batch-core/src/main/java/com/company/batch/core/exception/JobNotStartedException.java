package com.company.batch.core.exception;


public class JobNotStartedException extends RuntimeException {

    public JobNotStartedException(String message) {
        super(message);
    }
    public JobNotStartedException(String message, Throwable cause) {
        super(message, cause);
    }
}
