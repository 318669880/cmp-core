package com.fit2cloud.sdk.model;

public class F2CStack {
	private String stackId;
	private String stackName;
	private String description;
	private Long created;
	private Long lastUpdated;
	private String stackStatus;
	private String stackStatusReason;
	private String outputs;
	public String getStackId() {
		return stackId;
	}
	public void setStackId(String stackId) {
		this.stackId = stackId;
	}
	public String getStackName() {
		return stackName;
	}
	public void setStackName(String stackName) {
		this.stackName = stackName;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Long getCreated() {
		return created;
	}
	public void setCreated(Long created) {
		this.created = created;
	}
	public Long getLastUpdated() {
		return lastUpdated;
	}
	public void setLastUpdated(Long lastUpdated) {
		this.lastUpdated = lastUpdated;
	}
	public String getStackStatus() {
		return stackStatus;
	}
	public void setStackStatus(String stackStatus) {
		this.stackStatus = stackStatus;
	}
	public String getStackStatusReason() {
		return stackStatusReason;
	}
	public void setStackStatusReason(String stackStatusReason) {
		this.stackStatusReason = stackStatusReason;
	}
	public String getOutputs() {
		return outputs;
	}
	public void setOutputs(String outputs) {
		this.outputs = outputs;
	}
}
