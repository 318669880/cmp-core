package com.fit2cloud.sdk.model;

/**
 * Created by dongbin on 2017/7/27.
 */
public class GetSlbMetricRequest extends Request {
    private int period = 10;
    private Long timepoint;
    private String instanceId;
    private String port;
    private String vip;

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

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getVip() {
        return vip;
    }

    public void setVip(String vip) {
        this.vip = vip;
    }
}
