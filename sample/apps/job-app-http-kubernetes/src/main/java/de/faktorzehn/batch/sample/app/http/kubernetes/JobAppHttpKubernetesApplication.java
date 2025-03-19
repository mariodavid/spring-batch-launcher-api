package de.faktorzehn.batch.sample.app.http.kubernetes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;

import de.faktorzehn.batch.sample.app.http.kubernetes.config.KubernetesJobProperties;

@SpringBootApplication(scanBasePackages = {
        "de.faktorzehn.batch.sample.app.http.kubernetes",
        "de.faktorzehn.batch.extservice",
        "de.faktorzehn.batch.persistence",
})
@EnableConfigurationProperties(KubernetesJobProperties.class)
@EnableJdbcRepositories(basePackages = "de.faktorzehn.batch.persistence")
public class JobAppHttpKubernetesApplication {

    public static void main(String[] args) {
        SpringApplication.run(JobAppHttpKubernetesApplication.class, args);
    }

}
