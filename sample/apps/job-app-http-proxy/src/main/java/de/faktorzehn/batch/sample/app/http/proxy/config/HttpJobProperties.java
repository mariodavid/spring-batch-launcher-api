package de.faktorzehn.batch.sample.app.http.proxy.config;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

import de.faktorzehn.batch.launcher.http.config.HttpJobConfiguration;

@ConfigurationProperties(prefix = "job.http")
public record HttpJobProperties(List<HttpJobConfiguration> configurations) {
}