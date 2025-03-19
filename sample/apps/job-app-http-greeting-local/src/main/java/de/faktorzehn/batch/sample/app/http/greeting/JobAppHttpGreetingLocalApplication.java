package de.faktorzehn.batch.sample.app.http.greeting;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;

@SpringBootApplication(scanBasePackages = {
        "de.faktorzehn.batch.sample.app.http.greeting",
        "de.faktorzehn.batch.sample.job.greeting",
        "de.faktorzehn.batch.extservice",
        "de.faktorzehn.batch.persistence",
})
@EnableJdbcRepositories(basePackages = "de.faktorzehn.batch.persistence")
public class JobAppHttpGreetingLocalApplication {

    public static void main(String[] args) {
        SpringApplication.run(JobAppHttpGreetingLocalApplication.class, args);
    }

}
