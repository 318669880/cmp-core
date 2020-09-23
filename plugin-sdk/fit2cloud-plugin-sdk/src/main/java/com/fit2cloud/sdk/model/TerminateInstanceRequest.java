package com.fit2cloud.sdk.model;

public class TerminateInstanceRequest extends Request {
	private String instanceId;
	private boolean keepDiskWhenTerminate;
	private String customData;
	private String zone;

	private String projectId;

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public String getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}

	public boolean isKeepDiskWhenTerminate() {
		return keepDiskWhenTerminate;
	}

	public void setKeepDiskWhenTerminate(boolean keepDiskWhenTerminate) {
		this.keepDiskWhenTerminate = keepDiskWhenTerminate;
	}

	public String getCustomData() {
		return customData;
	}

	public void setCustomData(String customData) {
		this.customData = customData;
	}

	public String getZone() {
		return zone;
	}

	public void setZone(String zone) {
		this.zone = zone;
	}
}
