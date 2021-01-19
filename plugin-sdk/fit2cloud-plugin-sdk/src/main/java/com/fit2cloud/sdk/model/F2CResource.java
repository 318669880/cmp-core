package com.fit2cloud.sdk.model;

import com.alibaba.fastjson.JSONObject;

public class F2CResource {
	private String resourceId;
	private JSONObject customdata;


	public JSONObject getCustomdata() {
		return customdata;
	}

	public void setCustomdata(JSONObject customdata) {
		this.customdata = customdata;
	}

	public String getResourceId() {
		return resourceId;
	}

	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}
}
