package com.fit2cloud.commons.server.model;

import com.fit2cloud.commons.server.constants.F2CMetricName;
import com.fit2cloud.commons.server.constants.MetricSource;
import io.swagger.annotations.ApiModelProperty;

public class MetricDataRequest {
    @ApiModelProperty("资源 ID")
    private String resourceId; //
    @ApiModelProperty("监控指标名称")
    private F2CMetricName metric;     //
    @ApiModelProperty("监控来源")
    private MetricSource metricSource;
    @ApiModelProperty("promQL")
    private String promQL;

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public MetricSource getMetricSource() {
        return metricSource;
    }

    public void setMetricSource(MetricSource metricSource) {
        this.metricSource = metricSource;
    }

    public F2CMetricName getMetric() {
        return metric;
    }

    public void setMetric(F2CMetricName metric) {
        this.metric = metric;
    }

    public String getPromQL() {
        return promQL;
    }

    public void setPromQL(String promQL) {
        this.promQL = promQL;
    }
}
