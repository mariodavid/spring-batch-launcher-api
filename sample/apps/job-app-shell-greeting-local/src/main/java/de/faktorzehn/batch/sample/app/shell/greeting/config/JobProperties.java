package de.faktorzehn.batch.sample.app.shell.greeting.config;

import jakarta.validation.constraints.NotBlank;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import de.faktorzehn.batch.sample.job.greeting.GreetingJobParameters;

@Validated
@ConfigurationProperties(prefix = "job")
public record JobProperties(
        @NotBlank(message = "External Job Execution ID must not be blank")
        String externalJobExecutionId
) {

}