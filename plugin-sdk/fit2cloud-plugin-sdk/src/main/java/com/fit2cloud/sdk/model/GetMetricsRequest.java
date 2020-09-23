package com.fit2cloud.sdk.model;

public class GetMetricsRequest extends Request {
	private int period = 10;
	private long timepoint;
	private String[] hosts;
	private int meticLimitOneRequest;

    public long getTimepoint() {
        return timepoint;
    }

    public void setTimepoint(long timepoint) {
        this.timepoint = timepoint;
    }

    public String[] getHosts() {
		return hosts;
	}

	public void setHosts(String[] hosts) {
		this.hosts = hosts;
	}

	public int getPeriod() {
		return period;
	}

	public void setPeriod(int period) {
		this.period = period;
	}

	public int getMeticLimitOneRequest() {
		return meticLimitOneRequest;
	}

	public void setMeticLimitOneRequest(int meticLimitOneRequest) {
		this.meticLimitOneRequest = meticLimitOneRequest;
	}
}
