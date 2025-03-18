package de.faktorzehn.batch.sample.app.http.greeting;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
        "de.faktorzehn.batch.sample.app.http.greeting",
        "de.faktorzehn.batch.sample.job.greeting",
        "de.faktorzehn.batch.extservice"
})
public class JobAppHttpGreetingLocalApplication {

    public static void main(String[] args) {
        SpringApplication.run(JobAppHttpGreetingLocalApplication.class, args);
    }

}
