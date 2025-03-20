package de.faktorzehn.batch.sample.app.http.docker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;

import de.faktorzehn.batch.sample.app.http.docker.config.DockerJobProperties;

@SpringBootApplication(scanBasePackages = {
        "de.faktorzehn.batch.sample.app.http.docker",
        "de.faktorzehn.batch.extservice",
        "de.faktorzehn.batch.persistence",
})
@EnableConfigurationProperties(DockerJobProperties.class)
@EnableJdbcRepositories(basePackages = "de.faktorzehn.batch.persistence")
public class JobAppHttpDockerApplication {

    public static void main(String[] args) {
        SpringApplication.run(JobAppHttpDockerApplication.class, args);
    }

}
