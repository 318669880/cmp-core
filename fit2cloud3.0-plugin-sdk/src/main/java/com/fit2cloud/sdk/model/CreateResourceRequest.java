package com.fit2cloud.sdk.model;
/**
 * 创建资源请求实体
 */
public class CreateResourceRequest extends Request {
	private String resourceType;

	public String getResourceType() {
		return resourceType;
	}

	public void setResourceType(String resourceType) {
		this.resourceType = resourceType;
	}
}
