package com.fit2cloud.sdk.model;

public class F2CRouter extends F2CResource {
	private String id;
	private String name;
	private String subnetId;
	private String subnetName;
	private String netPubId;
	private String netPubName;
	private String fireWallId;
	private String fireWallName;
	private String availableZone;
	private String regionId;
	private String regionName;

	public F2CRouter() {
	}

	public F2CRouter(String id, String name, String subnetId, String subnetName, String netPubId, String netPubName,
			String fireWallId, String fireWallName, String availableZone) {
		this.id = id;
		this.name = name;
		this.subnetId = subnetId;
		this.subnetName = subnetName;
		this.netPubId = netPubId;
		this.netPubName = netPubName;
		this.fireWallId = fireWallId;
		this.fireWallName = fireWallName;
		this.availableZone = availableZone;
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

	public String getSubnetId() {
		return subnetId;
	}

	public void setSubnetId(String subnetId) {
		this.subnetId = subnetId;
	}

	public String getSubnetName() {
		return subnetName;
	}

	public void setSubnetName(String subnetName) {
		this.subnetName = subnetName;
	}

	public String getNetPubId() {
		return netPubId;
	}

	public void setNetPubId(String netPubId) {
		this.netPubId = netPubId;
	}

	public String getNetPubName() {
		return netPubName;
	}

	public void setNetPubName(String netPubName) {
		this.netPubName = netPubName;
	}

	public String getFireWallId() {
		return fireWallId;
	}

	public void setFireWallId(String fireWallId) {
		this.fireWallId = fireWallId;
	}

	public String getFireWallName() {
		return fireWallName;
	}

	public void setFireWallName(String fireWallName) {
		this.fireWallName = fireWallName;
	}

	public String getAvailableZone() {
		return availableZone;
	}

	public void setAvailableZone(String availableZone) {
		this.availableZone = availableZone;
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

}
