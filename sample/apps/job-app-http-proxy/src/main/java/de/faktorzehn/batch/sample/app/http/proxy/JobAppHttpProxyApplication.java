package de.faktorzehn.batch.sample.app.http.proxy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;

import de.faktorzehn.batch.sample.app.http.proxy.config.HttpJobProperties;

@SpringBootApplication(scanBasePackages = {
        "de.faktorzehn.batch.sample.app.http.proxy",
        "de.faktorzehn.batch.extservice",
        "de.faktorzehn.batch.persistence",
})
@EnableConfigurationProperties(HttpJobProperties.class)
@EnableJdbcRepositories(basePackages = "de.faktorzehn.batch.persistence")
public class JobAppHttpProxyApplication {

    public static void main(String[] args) {
        SpringApplication.run(JobAppHttpProxyApplication.class, args);
    }

}
