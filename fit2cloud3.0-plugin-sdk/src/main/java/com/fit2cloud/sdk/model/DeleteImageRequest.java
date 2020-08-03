package com.fit2cloud.sdk.model;
/**
 * 删除镜像请求实体
 */
public class DeleteImageRequest extends Request {
	private String imageId;

	public String getImageId() {
		return imageId;
	}

	public void setImageId(String imageId) {
		this.imageId = imageId;
	}
}
