package com.fit2cloud.sdk.model;

public class F2CNetworkInterface extends F2CResource {
    private String id;
    private String name;
    private String ipAddress;
    private String subnetId;
    private String networkId;
    private String subnetName;
    private String securityGroupId;
    private String securityGroupName;
    private String status;
    private String regionId;

    public String getRegionId() {
        return this.regionId;
    }

    public void setRegionId(String regionId) {
        this.regionId = regionId;
    }

    public F2CNetworkInterface() {}

    public F2CNetworkInterface(String id, String name, String ipAddress, String subnetId, String networkId,
        String subnetName, String securityGroupId, String securityGroupName) {
        this.id = id;
        this.name = name;
        this.ipAddress = ipAddress;
        this.subnetId = subnetId;
        this.networkId = networkId;
        this.subnetName = subnetName;
        this.securityGroupId = securityGroupId;
        this.securityGroupName = securityGroupName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;

    }

    public String getSecurityGroupName() {
        return securityGroupName;
    }

    public void setSecurityGroupName(String securityGroupName) {
        this.securityGroupName = securityGroupName;
    }

    public String getSecurityGroupId() {
        return securityGroupId;
    }

    public void setSecurityGroupId(String securityGroupId) {
        this.securityGroupId = securityGroupId;
    }

    public String getSubnetName() {
        return subnetName;
    }

    public void setSubnetName(String subnetName) {
        this.subnetName = subnetName;
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

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getSubnetId() {
        return subnetId;
    }

    public void setSubnetId(String subnetId) {
        this.subnetId = subnetId;
    }

    public String getNetworkId() {
        return networkId;
    }

    public void setNetworkId(String networkId) {
        this.networkId = networkId;
    }

}
