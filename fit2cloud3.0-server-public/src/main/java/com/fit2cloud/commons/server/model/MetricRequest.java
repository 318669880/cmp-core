package com.fit2cloud.commons.server.model;

import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.List;

public class MetricRequest {
    @ApiModelProperty("监控查询 Request")
    private List<MetricDataRequest> metricDataQueries = new ArrayList<>();
    @ApiModelProperty("开始时间")
    private long startTime;
    @ApiModelProperty("结束时间")
    private long endTime;
    @ApiModelProperty("步长")
    private int step = 3600;

    public List<MetricDataRequest> getMetricDataQueries() {
        return metricDataQueries;
    }

    public void setMetricDataQueries(List<MetricDataRequest> metricDataQueries) {
        this.metricDataQueries = metricDataQueries;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }
}
