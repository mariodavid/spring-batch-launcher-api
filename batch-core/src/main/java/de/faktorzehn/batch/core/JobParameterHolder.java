package de.faktorzehn.batch.core;

import java.util.Map;

public interface JobParameterHolder {

    Map<String, Object> getParameters();
}
