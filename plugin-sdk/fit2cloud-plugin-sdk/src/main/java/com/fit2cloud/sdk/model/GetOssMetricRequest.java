package com.fit2cloud.sdk.model;

/**
 * Created by dongbin on 2017/7/27.
 */
public class GetOssMetricRequest extends Request{
    private int period = 60;
    private Long timepoint;

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
}
