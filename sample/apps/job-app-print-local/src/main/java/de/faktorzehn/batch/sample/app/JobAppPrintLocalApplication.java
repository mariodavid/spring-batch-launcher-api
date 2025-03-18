package de.faktorzehn.batch.sample.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
        "de.faktorzehn.batch.sample.app",
        "de.faktorzehn.batch.sample.job.print",
        "de.faktorzehn.batch.extservice"
})
public class JobAppPrintLocalApplication {

    public static void main(String[] args) {
        SpringApplication.run(JobAppPrintLocalApplication.class, args);
    }

}
