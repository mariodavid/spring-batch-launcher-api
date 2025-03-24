package com.company.batch.sample.app.shell.greeting;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;

@SpringBootApplication(scanBasePackages = {
        "com.company.batch.sample.app.shell.greeting",
        "com.company.batch.sample.job.greeting",
        "com.company.batch.persistence",
        "com.company.batch.execution",
})
@EnableJdbcRepositories(basePackages = "com.company.batch.persistence")
public class JobAppShellGreetingLocalApplication {

    private static final Logger log = LoggerFactory.getLogger(JobAppShellGreetingLocalApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(JobAppShellGreetingLocalApplication.class, args);
    }


}
