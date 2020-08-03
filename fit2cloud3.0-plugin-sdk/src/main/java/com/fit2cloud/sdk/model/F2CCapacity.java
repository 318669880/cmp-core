package com.fit2cloud.sdk.model;

/**
 * Created by liqiang on 2018/4/11.
 */
public class F2CCapacity {

    //CPU
    private long cpuTotal;

    private long cpuUsed;

    private long cpuUnUsed;

    private long cpuUsedPercentage;

    private long cpuUnUsedPercentage;

    private String cpuUnit = "GHz";


    //MEMORY
    private long memTotal;

    private long memUsed;

    private long memUnUsed;

    private long memUsedPercentage;

    private long memUnUsedPercentage;

    private String memUnit = "GB";


    //STORAGE
    private long storageTotal;

    private long storageUsed;

    private long storageUnUsed;

    private long storageUsedPercentage;

    private long storageUnUsedPercentage;

    private String storageUnit = "GB";

    public long getCpuTotal() {
        return cpuTotal;
    }

    public void setCpuTotal(long cpuTotal) {
        this.cpuTotal = cpuTotal;
    }

    public long getCpuUsed() {
        return cpuUsed;
    }

    public void setCpuUsed(long cpuUsed) {
        this.cpuUsed = cpuUsed;
    }

    public long getCpuUnUsed() {
        return cpuUnUsed;
    }

    public void setCpuUnUsed(long cpuUnUsed) {
        this.cpuUnUsed = cpuUnUsed;
    }

    public long getCpuUsedPercentage() {
        return cpuUsedPercentage;
    }

    public void setCpuUsedPercentage(long cpuUsedPercentage) {
        this.cpuUsedPercentage = cpuUsedPercentage;
    }

    public long getCpuUnUsedPercentage() {
        return cpuUnUsedPercentage;
    }

    public void setCpuUnUsedPercentage(long cpuUnUsedPercentage) {
        this.cpuUnUsedPercentage = cpuUnUsedPercentage;
    }

    public String getCpuUnit() {
        return cpuUnit;
    }

    public void setCpuUnit(String cpuUnit) {
        this.cpuUnit = cpuUnit;
    }

    public long getMemTotal() {
        return memTotal;
    }

    public void setMemTotal(long memTotal) {
        this.memTotal = memTotal;
    }

    public long getMemUsed() {
        return memUsed;
    }

    public void setMemUsed(long memUsed) {
        this.memUsed = memUsed;
    }

    public long getMemUnUsed() {
        return memUnUsed;
    }

    public void setMemUnUsed(long memUnUsed) {
        this.memUnUsed = memUnUsed;
    }

    public long getMemUsedPercentage() {
        return memUsedPercentage;
    }

    public void setMemUsedPercentage(long memUsedPercentage) {
        this.memUsedPercentage = memUsedPercentage;
    }

    public long getMemUnUsedPercentage() {
        return memUnUsedPercentage;
    }

    public void setMemUnUsedPercentage(long memUnUsedPercentage) {
        this.memUnUsedPercentage = memUnUsedPercentage;
    }

    public String getMemUnit() {
        return memUnit;
    }

    public void setMemUnit(String memUnit) {
        this.memUnit = memUnit;
    }

    public long getStorageTotal() {
        return storageTotal;
    }

    public void setStorageTotal(long storageTotal) {
        this.storageTotal = storageTotal;
    }

    public long getStorageUsed() {
        return storageUsed;
    }

    public void setStorageUsed(long storageUsed) {
        this.storageUsed = storageUsed;
    }

    public long getStorageUnUsed() {
        return storageUnUsed;
    }

    public void setStorageUnUsed(long storageUnUsed) {
        this.storageUnUsed = storageUnUsed;
    }

    public long getStorageUsedPercentage() {
        return storageUsedPercentage;
    }

    public void setStorageUsedPercentage(long storageUsedPercentage) {
        this.storageUsedPercentage = storageUsedPercentage;
    }

    public long getStorageUnUsedPercentage() {
        return storageUnUsedPercentage;
    }

    public void setStorageUnUsedPercentage(long storageUnUsedPercentage) {
        this.storageUnUsedPercentage = storageUnUsedPercentage;
    }

    public String getStorageUnit() {
        return storageUnit;
    }

    public void setStorageUnit(String storageUnit) {
        this.storageUnit = storageUnit;
    }
}
