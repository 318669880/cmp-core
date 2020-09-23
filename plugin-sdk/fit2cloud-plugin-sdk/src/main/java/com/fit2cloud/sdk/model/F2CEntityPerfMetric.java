package com.fit2cloud.sdk.model;

import java.util.List;

/**
 * 宿主机/虚拟机监控实体
 */
public class F2CEntityPerfMetric {
    private String credentialId;
    private String credentialName;
    private String datacenterName;
    private String clusterName;
    private String hostName;
    private String virtualMachineName;
    private String cloudUniqueId; // required
    private F2CEntityType entityType; // required
    private double cpuUsage;        //percent
    private int cpuUsageInMhz;
    private int memoryUsage; // percent
    private int memoryGranted;
    private long diskUsage;
    private long diskProvisioned;
    private int hostCpuInMHZ;
    private int hostMemoryInMB;
    private int vmMemoryAllocatedSize;
    private int vmDiskAllocatedSize;
    private int vmCpuMHZAllocatedSize;
    private long computingSampleTimestamp;
    private long createdTimestamp;
    private String powerState;
    private int bytesReceivedPerSecond;    //kiloBytesPerSecond
    private int bytesTransmittedPerSecond; //kiloBytesPerSecond
    private int diskReadAverage; //kiloBytesPerSecond
    private int diskWriteAverage; //kiloBytesPerSecond
    private F2CVmDatastoreUsage[] datastoreUsages;
    private List<String[]> virtualDiskReadLatencyAverageList;
    private List<String[]> virtualDiskWriteLatencyAverageList;
    private int virtualDiskReadLatencyAverage; //millisecond
    private int virtualDiskWriteLatencyAverage; //millisecond
    private long memoryUsageInMB;

    public F2CEntityType getEntityType() {
        return entityType;
    }

    public void setEntityType(F2CEntityType entityType) {
        this.entityType = entityType;
    }

    public String getCredentialId() {
        return credentialId;
    }

    public void setCredentialId(String credentialId) {
        this.credentialId = credentialId;
    }

    public String getCredentialName() {
        return credentialName;
    }

    public void setCredentialName(String credentialName) {
        this.credentialName = credentialName;
    }

    public String getDatacenterName() {
        return datacenterName;
    }

    public void setDatacenterName(String datacenterName) {
        this.datacenterName = datacenterName;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public double getCpuUsage() {
        return cpuUsage;
    }

    public void setCpuUsage(double cpuUsage) {
        this.cpuUsage = cpuUsage;
    }

    public int getCpuUsageInMhz() {
        return cpuUsageInMhz;
    }

    public void setCpuUsageInMhz(int cpuUsageInMhz) {
        this.cpuUsageInMhz = cpuUsageInMhz;
    }

    public int getMemoryUsage() {
        return memoryUsage;
    }

    public void setMemoryUsage(int memoryUsage) {
        this.memoryUsage = memoryUsage;
    }

    public int getMemoryGranted() {
        return memoryGranted;
    }

    public void setMemoryGranted(int memoryGranted) {
        this.memoryGranted = memoryGranted;
    }

    public long getDiskUsage() {
        return diskUsage;
    }

    public void setDiskUsage(long diskUsage) {
        this.diskUsage = diskUsage;
    }

    public long getDiskProvisioned() {
        return diskProvisioned;
    }

    public void setDiskProvisioned(long diskProvisioned) {
        this.diskProvisioned = diskProvisioned;
    }

    public long getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(long createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public String getVirtualMachineName() {
        return virtualMachineName;
    }

    public void setVirtualMachineName(String virtualMachineName) {
        this.virtualMachineName = virtualMachineName;
    }

    public long getComputingSampleTimestamp() {
        return computingSampleTimestamp;
    }

    public void setComputingSampleTimestamp(long computingSampleTimestamp) {
        this.computingSampleTimestamp = computingSampleTimestamp;
    }

    public int getVmMemoryAllocatedSize() {
        return vmMemoryAllocatedSize;
    }

    public void setVmMemoryAllocatedSize(int vmMemoryAllocatedSize) {
        this.vmMemoryAllocatedSize = vmMemoryAllocatedSize;
    }

    public int getVmDiskAllocatedSize() {
        return vmDiskAllocatedSize;
    }

    public void setVmDiskAllocatedSize(int vmDiskAllocatedSize) {
        this.vmDiskAllocatedSize = vmDiskAllocatedSize;
    }

    public int getVmCpuMHZAllocatedSize() {
        return vmCpuMHZAllocatedSize;
    }

    public void setVmCpuMHZAllocatedSize(int vmCpuMHZAllocatedSize) {
        this.vmCpuMHZAllocatedSize = vmCpuMHZAllocatedSize;
    }

    public int getHostCpuInMHZ() {
        return hostCpuInMHZ;
    }

    public void setHostCpuInMHZ(int hostCpuInMHZ) {
        this.hostCpuInMHZ = hostCpuInMHZ;
    }

    public int getHostMemoryInMB() {
        return hostMemoryInMB;
    }

    public void setHostMemoryInMB(int hostMemoryInMB) {
        this.hostMemoryInMB = hostMemoryInMB;
    }

    public String getPowerState() {
        return powerState;
    }

    public void setPowerState(String powerState) {
        this.powerState = powerState;
    }

    public int getBytesReceivedPerSecond() {
        return bytesReceivedPerSecond;
    }

    public void setBytesReceivedPerSecond(int bytesReceivedPerSecond) {
        this.bytesReceivedPerSecond = bytesReceivedPerSecond;
    }

    public int getBytesTransmittedPerSecond() {
        return bytesTransmittedPerSecond;
    }

    public void setBytesTransmittedPerSecond(int bytesTransmittedPerSecond) {
        this.bytesTransmittedPerSecond = bytesTransmittedPerSecond;
    }

    public String getCloudUniqueId() {
        return cloudUniqueId;
    }

    public void setCloudUniqueId(String cloudUniqueId) {
        this.cloudUniqueId = cloudUniqueId;
    }

    public int getDiskReadAverage() {
        return diskReadAverage;
    }

    public void setDiskReadAverage(int diskReadAverage) {
        this.diskReadAverage = diskReadAverage;
    }

    public int getDiskWriteAverage() {
        return diskWriteAverage;
    }

    public void setDiskWriteAverage(int diskWriteAverage) {
        this.diskWriteAverage = diskWriteAverage;
    }

    public F2CVmDatastoreUsage[] getDatastoreUsages() {
        return datastoreUsages;
    }

    public void setDatastoreUsages(F2CVmDatastoreUsage[] datastoreUsages) {
        this.datastoreUsages = datastoreUsages;
    }

    public List<String[]> getVirtualDiskReadLatencyAverageList() {
        return virtualDiskReadLatencyAverageList;
    }

    public void setVirtualDiskReadLatencyAverageList(List<String[]> virtualDiskReadLatencyAverageList) {
        this.virtualDiskReadLatencyAverageList = virtualDiskReadLatencyAverageList;
    }

    public List<String[]> getVirtualDiskWriteLatencyAverageList() {
        return virtualDiskWriteLatencyAverageList;
    }

    public void setVirtualDiskWriteLatencyAverageList(List<String[]> virtualDiskWriteLatencyAverageList) {
        this.virtualDiskWriteLatencyAverageList = virtualDiskWriteLatencyAverageList;
    }

    public int getVirtualDiskReadLatencyAverage() {
        return virtualDiskReadLatencyAverage;
    }

    public void setVirtualDiskReadLatencyAverage(int virtualDiskReadLatencyAverage) {
        this.virtualDiskReadLatencyAverage = virtualDiskReadLatencyAverage;
    }

    public int getVirtualDiskWriteLatencyAverage() {
        return virtualDiskWriteLatencyAverage;
    }

    public void setVirtualDiskWriteLatencyAverage(int virtualDiskWriteLatencyAverage) {
        this.virtualDiskWriteLatencyAverage = virtualDiskWriteLatencyAverage;
    }

    public long getMemoryUsageInMB() {
        return memoryUsageInMB;
    }

    public void setMemoryUsageInMB(long memoryUsageInMB) {
        this.memoryUsageInMB = memoryUsageInMB;
    }
}
