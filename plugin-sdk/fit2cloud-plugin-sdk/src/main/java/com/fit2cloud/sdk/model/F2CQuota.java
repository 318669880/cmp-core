package com.fit2cloud.sdk.model;

public class F2CQuota {

    private int vmCount = 0;
    private int vmCpuCount = 0;
    private int vmMemSize = 0;
    private int vmDiskSize = 0;
    private int floatIpCount = 0;
    private String regionId;

    public String getRegionId() {
        return this.regionId;
    }

    public void setRegionId(String regionId) {
        this.regionId = regionId;
    }

    public int getVmCount() {
        return vmCount;
    }

    public void setVmCount(int vmCount) {
        this.vmCount = vmCount;
    }

    public int getVmCpuCount() {
        return vmCpuCount;
    }

    public void setVmCpuCount(int vmCpuCount) {
        this.vmCpuCount = vmCpuCount;
    }

    public int getVmMemSize() {
        return vmMemSize;
    }

    public void setVmMemSize(int vmMemSize) {
        this.vmMemSize = vmMemSize;
    }

    public int getVmDiskSize() {
        return vmDiskSize;
    }

    public void setVmDiskSize(int vmDiskSize) {
        this.vmDiskSize = vmDiskSize;
    }

    public int getFloatIpCount() {
        return floatIpCount;
    }

    public void setFloatIpCount(int floatIpCount) {
        this.floatIpCount = floatIpCount;
    }
}
