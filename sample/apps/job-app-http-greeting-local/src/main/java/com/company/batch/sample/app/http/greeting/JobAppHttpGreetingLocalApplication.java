package com.company.batch.sample.app.http.greeting;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;

@SpringBootApplication(scanBasePackages = {
        "com.company.batch.sample.app.http.greeting",
        "com.company.batch.sample.job.greeting",
        "com.company.batch.extservice",
        "com.company.batch.persistence",
})
@EnableJdbcRepositories(basePackages = "com.company.batch.persistence")
public class JobAppHttpGreetingLocalApplication {

    public static void main(String[] args) {
        SpringApplication.run(JobAppHttpGreetingLocalApplication.class, args);
    }

}
