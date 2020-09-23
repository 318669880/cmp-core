package com.fit2cloud.sdk.model;
/**
 * 数据存储监控实体
 */
public class F2CDatastoreMetric {
    private String credentialId;
    private String credentialName;
    private String datacenterName;
    private String clusterName;
    private String datastoreName;
    private String cloudUniqueId;
    private long dataStoreTotalSpace;
    private long dataStoreSpaceUsed;
    private long dataStoreFreeSize;
    private long dataStoreProvisioned;
    private long dataStoreDiskFileUsed;
    private long dataStoreDeltaFileUsed;
    private long dataStoreSwapFileUsed;
    private long dataStoreOtherVMFileUsed;
    private long diskSampleTimestamp;

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

    public String getDatastoreName() {
        return datastoreName;
    }

    public void setDatastoreName(String datastoreName) {
        this.datastoreName = datastoreName;
    }

    public long getDataStoreTotalSpace() {
        return dataStoreTotalSpace;
    }

    public void setDataStoreTotalSpace(long dataStoreTotalSpace) {
        this.dataStoreTotalSpace = dataStoreTotalSpace;
    }

    public long getDataStoreSpaceUsed() {
        return dataStoreSpaceUsed;
    }

    public void setDataStoreSpaceUsed(long dataStoreSpaceUsed) {
        this.dataStoreSpaceUsed = dataStoreSpaceUsed;
    }

    public long getDataStoreFreeSize() {
        return dataStoreFreeSize;
    }

    public void setDataStoreFreeSize(long dataStoreFreeSize) {
        this.dataStoreFreeSize = dataStoreFreeSize;
    }

    public long getDataStoreDiskFileUsed() {
        return dataStoreDiskFileUsed;
    }

    public void setDataStoreDiskFileUsed(long dataStoreDiskFileUsed) {
        this.dataStoreDiskFileUsed = dataStoreDiskFileUsed;
    }

    public long getDataStoreDeltaFileUsed() {
        return dataStoreDeltaFileUsed;
    }

    public void setDataStoreDeltaFileUsed(long dataStoreDeltaFileUsed) {
        this.dataStoreDeltaFileUsed = dataStoreDeltaFileUsed;
    }

    public long getDataStoreSwapFileUsed() {
        return dataStoreSwapFileUsed;
    }

    public void setDataStoreSwapFileUsed(long dataStoreSwapFileUsed) {
        this.dataStoreSwapFileUsed = dataStoreSwapFileUsed;
    }

    public long getDataStoreOtherVMFileUsed() {
        return dataStoreOtherVMFileUsed;
    }

    public void setDataStoreOtherVMFileUsed(long dataStoreOtherVMFileUsed) {
        this.dataStoreOtherVMFileUsed = dataStoreOtherVMFileUsed;
    }

    public long getDiskSampleTimestamp() {
        return diskSampleTimestamp;
    }

    public void setDiskSampleTimestamp(long diskSampleTimestamp) {
        this.diskSampleTimestamp = diskSampleTimestamp;
    }

    public String getCloudUniqueId() {
        return cloudUniqueId;
    }

    public void setCloudUniqueId(String cloudUniqueId) {
        this.cloudUniqueId = cloudUniqueId;
    }

    public long getDataStoreProvisioned() {
        return dataStoreProvisioned;
    }

    public void setDataStoreProvisioned(long dataStoreProvisioned) {
        this.dataStoreProvisioned = dataStoreProvisioned;
    }
}
