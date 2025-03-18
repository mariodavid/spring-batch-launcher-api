package de.faktorzehn.batchapi.controller;

import java.util.Map;

public record JobRequest(
    String jobName,
    Map<String, Object> jobParameters){}