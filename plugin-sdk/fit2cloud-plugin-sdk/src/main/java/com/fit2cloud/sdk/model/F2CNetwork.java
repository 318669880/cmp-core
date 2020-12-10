package com.fit2cloud.sdk.model;

public class F2CNetwork extends F2CResource {
	private String id;
	private String name;
	private String subnets;
	private String availableZone;
	private String createTime;
	private Boolean shared;
	private Boolean external;
	private String regionId;
	private String regionName;
	private String networkId;
	public F2CNetwork() {
	}

	public F2CNetwork(String id, String name, String subnets, String availableZone, String createTime, Boolean shared,
			Boolean external) {
		this.id = id;
		this.name = name;
		this.subnets = subnets;
		this.availableZone = availableZone;
		this.createTime = createTime;
		this.shared = shared;
		this.external = external;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSubnets() {
		return subnets;
	}

	public void setSubnets(String subnets) {
		this.subnets = subnets;
	}

	public String getAvailableZone() {
		return availableZone;
	}

	public void setAvailableZone(String availableZone) {
		this.availableZone = availableZone;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public Boolean getShared() {
		return shared;
	}

	public void setShared(Boolean shared) {
		this.shared = shared;
	}

	public Boolean getExternal() {
		return external;
	}

	public void setExternal(Boolean external) {
		this.external = external;
	}

	public String getRegionName() {
		return regionName;
	}

	public void setRegionName(String regionName) {
		this.regionName = regionName;
	}

	public String getRegionId() {
		return regionId;
	}

	public void setRegionId(String regionId) {
		this.regionId = regionId;
	}

	public String getNetworkId() {
		return networkId;
	}

	public void setNetworkId(String networkId) {
		this.networkId = networkId;
	}

}
