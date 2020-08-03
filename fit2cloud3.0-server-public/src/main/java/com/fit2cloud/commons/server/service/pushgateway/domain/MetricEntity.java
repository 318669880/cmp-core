package com.fit2cloud.commons.server.service.pushgateway.domain;

import java.util.HashMap;
import java.util.Map;

public class MetricEntity {
    private String resourceId;
    private String metricName;
    private String help;
    private Double value;
    private Map<String, String> labelMap = new HashMap<>();

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getMetricName() {
        return metricName;
    }

    public void setMetricName(String metricName) {
        this.metricName = metricName;
    }

    public String getHelp() {
        return help;
    }

    public void setHelp(String help) {
        this.help = help;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public Map<String, String> getLabelMap() {
        return labelMap;
    }

    public void setLabelMap(Map<String, String> labelMap) {
        this.labelMap = labelMap;
    }
}
