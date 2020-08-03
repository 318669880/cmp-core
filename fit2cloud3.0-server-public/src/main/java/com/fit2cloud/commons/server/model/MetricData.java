package com.fit2cloud.commons.server.model;


import io.swagger.annotations.ApiModelProperty;

import java.util.List;

public class MetricData {
    @ApiModelProperty("资源 ID")
    private String resourceId;
    @ApiModelProperty("监控值")
    private List<Double> values;
    @ApiModelProperty("时间戳")
    private List<Long> timestamps;
    @ApiModelProperty("监控指标")
    private String metric;
    @ApiModelProperty("单位")
    private String unit;

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public List<Double> getValues() {
        return values;
    }

    public void setValues(List<Double> values) {
        this.values = values;
    }

    public List<Long> getTimestamps() {
        return timestamps;
    }

    public void setTimestamps(List<Long> timestamps) {
        this.timestamps = timestamps;
    }

    public String getMetric() {
        return metric;
    }

    public void setMetric(String metric) {
        this.metric = metric;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
