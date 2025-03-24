package com.company.batch.sample.app.http.proxy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;

import com.company.batch.sample.app.http.proxy.config.HttpJobProperties;

@SpringBootApplication(scanBasePackages = {
        "com.company.batch.sample.app.http.proxy",
        "com.company.batch.extservice",
        "com.company.batch.persistence",
})
@EnableConfigurationProperties(HttpJobProperties.class)
@EnableJdbcRepositories(basePackages = "com.company.batch.persistence")
public class JobAppHttpProxyApplication {

    public static void main(String[] args) {
        SpringApplication.run(JobAppHttpProxyApplication.class, args);
    }

}
