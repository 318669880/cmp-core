package com.fit2cloud.sdk.model;

/**
 * Created by dongbin on 2017/7/27.
 */
public class GetECSMetricRequest extends Request {
    private int period = 10;
    private Long timepoint;
    private String instanceId;

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public Long getTimepoint() {
        return timepoint;
    }

    public void setTimepoint(Long timepoint) {
        this.timepoint = timepoint;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }
}
