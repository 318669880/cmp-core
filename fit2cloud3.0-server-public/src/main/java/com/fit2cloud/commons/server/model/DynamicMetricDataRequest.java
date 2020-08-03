package com.fit2cloud.commons.server.model;

import io.swagger.annotations.ApiModelProperty;

public class DynamicMetricDataRequest {
    private String seriesName;
    @ApiModelProperty("监控来源")
    private MetricQueryType metricQueryType;
    @ApiModelProperty("promQL")
    private String promQL;

    public String getSeriesName() {
        return seriesName;
    }

    public void setSeriesName(String seriesName) {
        this.seriesName = seriesName;
    }

    public MetricQueryType getMetricQueryType() {
        return metricQueryType;
    }

    public void setMetricQueryType(MetricQueryType metricQueryType) {
        this.metricQueryType = metricQueryType;
    }

    public String getPromQL() {
        return promQL;
    }

    public void setPromQL(String promQL) {
        this.promQL = promQL;
    }
}
