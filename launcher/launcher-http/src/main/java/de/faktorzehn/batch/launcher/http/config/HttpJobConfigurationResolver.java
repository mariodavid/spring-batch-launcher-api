package de.faktorzehn.batch.launcher.http.config;

public interface HttpJobConfigurationResolver {
    /**
     * Liefert für einen gegebenen Jobnamen die konfigurierte Base-URL.
     * @param jobName der Name des Jobs
     * @return die Base-URL oder null, wenn keine Konfiguration vorhanden ist.
     */
    String resolveBaseUrl(String jobName);
}