package de.faktorzehn.batch.sample.app.shell.greeting.config;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import de.faktorzehn.batch.sample.job.greeting.GreetingJobParameters;

@Validated
@ConfigurationProperties(prefix = "job.greeting")
public record GreetingJobProperties(
        @NotNull(message = "Timeout is required")
        @Min(value = 100, message = "Timeout must be at least 100")
        Long timeout,

        @NotBlank(message = "Greeting name must not be blank")
        String greetingName
) {
    public GreetingJobParameters toPrintJobParameters() {
        return new GreetingJobParameters(timeout, greetingName);
    }
}