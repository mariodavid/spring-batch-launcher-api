package de.faktorzehn.batch.sample.app.shell.greeting;

import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;

import de.faktorzehn.batch.core.JobLauncherService;
import de.faktorzehn.batch.core.exception.JobNotFoundException;
import de.faktorzehn.batch.persistence.AcceptedJob;
import de.faktorzehn.batch.persistence.AcceptedJobRepository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootApplication(scanBasePackages = {
        "de.faktorzehn.batch.sample.app.shell.greeting",
        "de.faktorzehn.batch.sample.job.greeting",
        "de.faktorzehn.batch.persistence",
        "de.faktorzehn.batch.execution",
})
@EnableJdbcRepositories(basePackages = "de.faktorzehn.batch.persistence")
public class JobAppShellGreetingLocalApplication {

    private static final Logger log = LoggerFactory.getLogger(JobAppShellGreetingLocalApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(JobAppShellGreetingLocalApplication.class, args);
    }


}
