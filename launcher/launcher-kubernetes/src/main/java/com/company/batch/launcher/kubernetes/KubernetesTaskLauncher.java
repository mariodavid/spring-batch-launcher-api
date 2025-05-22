package com.company.batch.launcher.kubernetes;

import java.util.Map;

import com.company.batch.core.TaskLauncher;
import com.company.batch.launcher.kubernetes.config.KubernetesJobConfigurationResolver;

import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.api.model.EnvVar;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.PodTemplateSpec;
import io.fabric8.kubernetes.api.model.batch.v1.Job;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.utils.Serialization;

public class KubernetesTaskLauncher implements TaskLauncher {

    private final KubernetesJobConfigurationResolver kubernetesJobConfigurationResolver;
    private final KubernetesClient kubernetesClient;
    private final String namespace = "default";
    private static final String CONFIG_MAP_KEY = "job.yaml";

    public KubernetesTaskLauncher(KubernetesClient kubernetesClient, KubernetesJobConfigurationResolver kubernetesJobConfigurationResolver) {
        this.kubernetesJobConfigurationResolver = kubernetesJobConfigurationResolver;
        this.kubernetesClient = kubernetesClient;
    }

    @Override
    public void launchTask(String externalJobExecutionId, String jobName, Map<String, Object> parameters) {

        Job job = createJobFromTemplate(jobName);

        PodTemplateSpec template = job.getSpec().getTemplate();

        if (template.getMetadata() == null) {
            template.setMetadata(new ObjectMeta());
        }

        template.getMetadata().getLabels().put(
                "com.company.batch.job.external-job-execution-id", externalJobExecutionId
        );

        template.getSpec().getContainers().forEach(container ->
                container.getEnv().add(new EnvVar("JOB_EXTERNAL_JOB_EXECUTION_ID", externalJobExecutionId, null))
        );

        kubernetesClient.batch().v1().jobs()
                .inNamespace(namespace)
                .resource(job)
                .create();
    }

    public Job createJobFromTemplate(String jobName) {
        ConfigMap configMap = kubernetesClient.configMaps()
                .inNamespace(namespace)
                .withName(kubernetesJobConfigurationResolver.resolveJobTemplateName(jobName))
                .get();

        if (configMap == null || !configMap.getData().containsKey(CONFIG_MAP_KEY)) {
            throw new IllegalStateException("Job-Template not found in ConfigMap: " + CONFIG_MAP_KEY);
        }

        String jobYaml = configMap.getData().get(CONFIG_MAP_KEY);

        Job job = Serialization.unmarshal(jobYaml, Job.class);

        job.getMetadata().setName(jobName.toLowerCase() + "-" + System.currentTimeMillis());

        return job;
    }

}