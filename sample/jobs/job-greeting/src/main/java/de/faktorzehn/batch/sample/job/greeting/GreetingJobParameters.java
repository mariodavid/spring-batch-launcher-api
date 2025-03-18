package de.faktorzehn.batch.sample.job.greeting;

import java.util.Map;

import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;

import de.faktorzehn.batch.core.JobParameterHolder;

public record GreetingJobParameters(Long timeout, String greetingName) implements JobParameterHolder {
    public static GreetingJobParameters fromJobParameters(Map<String, Object> jobParameters) {
        return new GreetingJobParameters((Long) jobParameters.get("timeout"), (String) jobParameters.get("greetingName"));
    }

    public static void fromJobParameters(JobParameters parameters) throws JobParametersInvalidException {
        Map<String, JobParameter<?>> paramMap = parameters.getParameters();
        if (!paramMap.containsKey("timeout")) {
            throw new JobParametersInvalidException("Missing required parameter: timeout");
        }
        if (!paramMap.containsKey("greetingName")) {
            throw new JobParametersInvalidException("Missing required parameter: greetingName");
        }

        JobParameter<?> timeoutParam = paramMap.get("timeout");
        JobParameter<?> greetingNameParam = paramMap.get("greetingName");

        Object timeoutValue = timeoutParam.getValue();
        if (!(timeoutValue instanceof Long)) {
            try {
                Long.parseLong(timeoutValue.toString());
            } catch (NumberFormatException e) {
                throw new JobParametersInvalidException("Parameter 'timeout' must be a Long");
            }
        }

        Object greetingNameValue = greetingNameParam.getValue();
        if (!(greetingNameValue instanceof String) || ((String) greetingNameValue).trim().isEmpty()) {
            throw new JobParametersInvalidException("Parameter 'greetingName' must be a non-empty String");
        }
    }


    @Override
    public Map<String, Object> getParameters() {
        return Map.of(
                "timeout", timeout,
                "greetingName", greetingName
        );
    }
}