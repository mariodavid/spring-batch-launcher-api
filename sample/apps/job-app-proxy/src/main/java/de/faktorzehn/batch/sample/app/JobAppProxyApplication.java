package de.faktorzehn.batch.sample.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import de.faktorzehn.batch.sample.app.config.HttpJobProperties;

@SpringBootApplication(scanBasePackages = {
        "de.faktorzehn.batch.sample.app",
        "de.faktorzehn.batch.extservice"
})
@EnableConfigurationProperties(HttpJobProperties.class)
public class JobAppProxyApplication {

    public static void main(String[] args) {
        SpringApplication.run(JobAppProxyApplication.class, args);
    }

}
