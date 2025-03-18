package de.faktorzehn.batch.core.exception;


public class JobExecutionCreationFailedException extends RuntimeException {

    public JobExecutionCreationFailedException(String message) {
        super(message);
    }
    public JobExecutionCreationFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
