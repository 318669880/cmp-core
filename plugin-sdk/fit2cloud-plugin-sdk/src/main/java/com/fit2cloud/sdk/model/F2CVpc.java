package com.fit2cloud.sdk.model;

public class F2CVpc extends F2CResource {
	private String vpcId;
	private String vpcName;
	private String cidrBlock;
	private String createTime;
	private String regionId;
	private String regionName;
	private String state;
	private String description;
	private Boolean isDefault;

	public F2CVpc() {
	}

	public F2CVpc(String vpcId, String vpcName, String cidrBlock, String createTime, String regionId,
			String regionName) {
		this.vpcId = vpcId;
		this.vpcName = vpcName;
		this.cidrBlock = cidrBlock;
		this.createTime = createTime;
		this.regionId = regionId;
		this.regionName = regionName;
	}

	public String getVpcId() {
		return vpcId;
	}

	public void setVpcId(String vpcId) {
		this.vpcId = vpcId;
	}

	public String getVpcName() {
		return vpcName;
	}

	public void setVpcName(String vpcName) {
		this.vpcName = vpcName;
	}

	public String getCidrBlock() {
		return cidrBlock;
	}

	public void setCidrBlock(String cidrBlock) {
		this.cidrBlock = cidrBlock;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public Boolean getIsDefault() {
		return isDefault;
	}

	public void setIsDefault(Boolean isDefault) {
		this.isDefault = isDefault;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getRegionId() {
		return regionId;
	}

	public void setRegionId(String regionId) {
		this.regionId = regionId;
	}

	public String getRegionName() {
		return regionName;
	}

	public void setRegionName(String regionName) {
		this.regionName = regionName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
}
