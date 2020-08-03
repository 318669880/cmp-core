package com.fit2cloud.sdk.model;

public class F2CSecurityGroup extends F2CResource {
	private String	securityGroupId;
	private String	description;
	private String	securityGroupName;
	private String	vpcId;
	private String	regionId;
	private String	regionName;
	private String	creationTime;
	private Integer	availableInstanceAmount;
	private Integer	ecsCount;
	
	public F2CSecurityGroup() {
	}
	
	public String getSecurityGroupId() {
		return this.securityGroupId;
	}
	
	public void setSecurityGroupId(String securityGroupId) {
		this.securityGroupId = securityGroupId;
	}
	
	public String getDescription() {
		return this.description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getSecurityGroupName() {
		return this.securityGroupName;
	}
	
	public void setSecurityGroupName(String securityGroupName) {
		this.securityGroupName = securityGroupName;
	}
	
	public String getVpcId() {
		return this.vpcId;
	}
	
	public void setVpcId(String vpcId) {
		this.vpcId = vpcId;
	}
	
	public String getRegionId() {
		return this.regionId;
	}
	
	public void setRegionId(String regionId) {
		this.regionId = regionId;
	}
	
	public String getRegionName() {
		return this.regionName;
	}
	
	public void setRegionName(String regionName) {
		this.regionName = regionName;
	}
	
	public String getCreationTime() {
		return this.creationTime;
	}
	
	public void setCreationTime(String creationTime) {
		this.creationTime = creationTime;
	}
	
	public Integer getAvailableInstanceAmount() {
		return this.availableInstanceAmount;
	}
	
	public void setAvailableInstanceAmount(Integer availableInstanceAmount) {
		this.availableInstanceAmount = availableInstanceAmount;
	}
	
	public Integer getEcsCount() {
		return this.ecsCount;
	}
	
	public void setEcsCount(Integer ecsCount) {
		this.ecsCount = ecsCount;
	}
	
}
