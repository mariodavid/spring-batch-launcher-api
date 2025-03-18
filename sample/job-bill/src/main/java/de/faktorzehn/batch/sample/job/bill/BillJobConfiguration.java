package de.faktorzehn.batch.sample.job.bill;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.batch.item.json.builder.JsonItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.transaction.PlatformTransactionManager;


import de.faktorzehn.batch.sample.job.bill.model.Bill;
import de.faktorzehn.batch.sample.job.bill.model.Usage;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class BillJobConfiguration {

    private static final Logger log = LoggerFactory.getLogger(BillJobConfiguration.class);

    @Bean
    public Job job(
            JobRepository jobRepository,
            ItemReader<Usage> reader,
            ItemProcessor<Usage, Bill> itemProcessor,
            ItemWriter<Bill> writer,
            PlatformTransactionManager transactionManager
    ) {
        Step step = new StepBuilder("billProcessing", jobRepository)
                .<Usage, Bill>chunk(1, transactionManager)
                .reader(reader)
                .processor(itemProcessor)
                .writer(writer)
                .build();

        return new JobBuilder("billJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(step)
                .build();
    }

    @Bean
    public JsonItemReader<Usage> jsonItemReader(@Value("classpath:usageinfo.json") Resource usageResource, ObjectMapper objectMapper) {

        JacksonJsonObjectReader<Usage> jsonObjectReader =
                new JacksonJsonObjectReader<>(Usage.class);
        jsonObjectReader.setMapper(objectMapper);

        return new JsonItemReaderBuilder<Usage>()
                .jsonObjectReader(jsonObjectReader)
                .resource(usageResource)
                .name("UsageJsonItemReader")
                .build();
    }

    @Bean
    public ItemWriter<Bill> jdbcBillWriter(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<Bill>()
                .beanMapped()
                .dataSource(dataSource)
                .sql("INSERT INTO BILL_STATEMENTS (id, first_name, " +
                        "last_name, minutes, data_usage,bill_amount) VALUES " +
                        "(:id, :firstName, :lastName, :minutes, :dataUsage, " +
                        ":billAmount)")
                .build();
    }

    @Bean
    ItemProcessor<Usage, Bill> billProcessor() {
        return usage -> {

            log.info("Processing usage: {}", usage);
            log.info("Calculating bill amount");
            Double billAmount = usage.getDataUsage() * .001 + usage.getMinutes() * .01;
            Thread.sleep(10_000);
            log.info("Bill amount calculated: {}", billAmount);

            return new Bill(
                    usage.getId(),
                    usage.getFirstName(),
                    usage.getLastName(),
                    usage.getDataUsage(),
                    usage.getMinutes(),
                    billAmount
            );
        };
    }

}