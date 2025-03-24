package com.company.batch.launcher.kubernetes.config;

public interface KubernetesJobConfigurationResolver {

    String resolveJobTemplateName(String jobName);
}