package com.company.batch.sample.app.http.kubernetes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;

import com.company.batch.sample.app.http.kubernetes.config.KubernetesJobProperties;

@SpringBootApplication(scanBasePackages = {
        "com.company.batch.sample.app.http.kubernetes",
        "com.company.batch.extservice",
        "com.company.batch.persistence",
})
@EnableConfigurationProperties(KubernetesJobProperties.class)
@EnableJdbcRepositories(basePackages = "com.company.batch.persistence")
public class JobAppHttpKubernetesApplication {

    public static void main(String[] args) {
        SpringApplication.run(JobAppHttpKubernetesApplication.class, args);
    }

}
