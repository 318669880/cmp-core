package com.fit2cloud.sdk.model;
/**
 * 创建镜像请求实体
 */
public class CreateImageRequest extends Request {
	private String instanceId;
	private String imageName;
	private String imageDescription;
	public String getInstanceId() {
		return instanceId;
	}
	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}
	public String getImageName() {
		return imageName;
	}
	public void setImageName(String imageName) {
		this.imageName = imageName;
	}
	public String getImageDescription() {
		return imageDescription;
	}
	public void setImageDescription(String imageDescription) {
		this.imageDescription = imageDescription;
	}
}
