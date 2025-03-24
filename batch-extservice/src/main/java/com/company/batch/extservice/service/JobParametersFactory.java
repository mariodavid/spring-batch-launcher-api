package com.company.batch.extservice.service;

import java.time.LocalDate;
import java.util.Date;
import java.util.Map;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;


public class JobParametersFactory {

    public static JobParameters convertToJobParameters(Map<String, Object> parameters) {
        JobParametersBuilder builder = new JobParametersBuilder();
        parameters.forEach((key, value) -> {
            switch (value) {
                case Number number -> builder.addLong(key, number.longValue());
                case Date date -> builder.addDate(key, date);
                case LocalDate localDate -> builder.addLocalDate(key, localDate);
                case null, default -> builder.addString(key, value != null ? value.toString() : "");
            }
        });
        return builder.toJobParameters();
    }

}