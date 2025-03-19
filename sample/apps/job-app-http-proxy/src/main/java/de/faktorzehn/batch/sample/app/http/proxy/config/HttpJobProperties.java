package de.faktorzehn.batch.sample.app.http.proxy.config;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "job.http")
public record HttpJobProperties(List<HttpJobConfiguration> configurations) {

    public record HttpJobConfiguration(String jobName, String baseUrl) {
    }
}