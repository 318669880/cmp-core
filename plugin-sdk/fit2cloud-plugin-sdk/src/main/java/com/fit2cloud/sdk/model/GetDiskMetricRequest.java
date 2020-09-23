package com.fit2cloud.sdk.model;

/**
 * 磁盘监控请求实体
 */
public class GetDiskMetricRequest extends GetMetricsRequest {
	private String diskId;

	public String getDiskId() {
		return diskId;
	}

	public void setDiskId(String diskId) {
		this.diskId = diskId;
	}

}
