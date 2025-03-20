package de.faktorzehn.batch.execution;

import jakarta.validation.constraints.NotBlank;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "job")
public record JobProperties(
        @NotBlank(message = "External Job Execution ID must not be blank")
        String externalJobExecutionId
) {

}