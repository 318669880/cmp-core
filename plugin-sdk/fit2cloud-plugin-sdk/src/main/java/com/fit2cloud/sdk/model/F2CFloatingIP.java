package com.fit2cloud.sdk.model;

/**
 * FIT2CLOUD定义的浮动IP模型
 */
public class F2CFloatingIP {
    private String ip;
    private String floatIpNetwork;
    private String floatIpNetworkName;
    private String name;
    private int totalIp;
    private int usedIp;
    private String status;
    private String instanceId;
    private String regionId;
    private String regionName;

    public String getRegionName() {
        return this.regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public String getRegionId() {
        return this.regionId;
    }

    public void setRegionId(String regionId) {
        this.regionId = regionId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getFloatIpNetworkName() {
        return floatIpNetworkName;
    }

    public void setFloatIpNetworkName(String floatIpNetworkName) {
        this.floatIpNetworkName = floatIpNetworkName;
    }

    public String getNetworkId() {
        return floatIpNetwork;
    }

    public void setNetworkId(String floatIpNetwork) {
        this.floatIpNetwork = floatIpNetwork;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTotalIp() {
        return totalIp;
    }

    public void setTotalIp(int totalIp) {
        this.totalIp = totalIp;
    }

    public int getUsedIp() {
        return usedIp;
    }

    public void setUsedIp(int usedIp) {
        this.usedIp = usedIp;
    }
}
