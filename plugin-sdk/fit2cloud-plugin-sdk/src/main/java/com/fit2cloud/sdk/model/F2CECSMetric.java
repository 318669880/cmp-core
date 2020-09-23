package com.fit2cloud.sdk.model;

import java.math.BigDecimal;
/**
 * ECS监控实体
 */
public class F2CECSMetric {
    private double cpuUtilization;
    private BigDecimal internetInRate;
    private BigDecimal intranetInRate;
    private BigDecimal internetOutRate;
    private BigDecimal intranetOutRate;
    private double internetOutRatePercent;
    private BigDecimal diskReadBPS;
    private BigDecimal diskWriteBPS;
    private double diskReadIOPS;
    private double diskWriteIOPS;
    private BigDecimal vpcPublicIPInternetInRate;
    private BigDecimal vpcPublicIPInternetOutRate;
    private double vpcPublicIPInternetOutRatePercent;

    private double cpuIdle;
    private double cpuSystem;
    private double cpuUser;
    private double cpuWait;
    private double cpuOther;
    private double cpuTotal;
    private BigDecimal memoryTotalspace;
    private BigDecimal memoryUsedspace;
    private BigDecimal memoryActualusedspace;
    private BigDecimal memoryFreespace;
    private double memoryFreeutilization;
    private double memoryUsedutilization;
    private String load1m;
    private String load5m;
    private String load15m;
    private BigDecimal diskusageUsed;
    private double diskusageUtilization;
    private BigDecimal diskusageFree;
    private BigDecimal diskusageTotal;
    private BigDecimal diskReadbytes;
    private BigDecimal diskWritebytes;
    private long diskReadiops;
    private long diskWriteiops;
    private double fsInodeutilization;
    private BigDecimal networkinRate;
    private BigDecimal networkoutRate;
    private long networkinPackages;
    private long networkoutPackages;
    private long networkinErrorpackages;
    private long networkoutErrorpackages;
    private long netTcpconnection;

    private int bytesReceivedPerSecond;
    private int bytesTransmittedPerSecond;

    public double getCpuUtilization() {
        return cpuUtilization;
    }

    public void setCpuUtilization(double cpuUtilization) {
        this.cpuUtilization = cpuUtilization;
    }

    public BigDecimal getInternetInRate() {
        return internetInRate;
    }

    public void setInternetInRate(BigDecimal internetInRate) {
        this.internetInRate = internetInRate;
    }

    public BigDecimal getIntranetInRate() {
        return intranetInRate;
    }

    public void setIntranetInRate(BigDecimal intranetInRate) {
        this.intranetInRate = intranetInRate;
    }

    public BigDecimal getInternetOutRate() {
        return internetOutRate;
    }

    public void setInternetOutRate(BigDecimal internetOutRate) {
        this.internetOutRate = internetOutRate;
    }

    public BigDecimal getIntranetOutRate() {
        return intranetOutRate;
    }

    public void setIntranetOutRate(BigDecimal intranetOutRate) {
        this.intranetOutRate = intranetOutRate;
    }

    public double getInternetOutRatePercent() {
        return internetOutRatePercent;
    }

    public void setInternetOutRatePercent(double internetOutRatePercent) {
        this.internetOutRatePercent = internetOutRatePercent;
    }

    public BigDecimal getDiskReadBPS() {
        return diskReadBPS;
    }

    public void setDiskReadBPS(BigDecimal diskReadBPS) {
        this.diskReadBPS = diskReadBPS;
    }

    public BigDecimal getDiskWriteBPS() {
        return diskWriteBPS;
    }

    public void setDiskWriteBPS(BigDecimal diskWriteBPS) {
        this.diskWriteBPS = diskWriteBPS;
    }

    public double getDiskReadIOPS() {
        return diskReadIOPS;
    }

    public void setDiskReadIOPS(double diskReadIOPS) {
        this.diskReadIOPS = diskReadIOPS;
    }

    public double getDiskWriteIOPS() {
        return diskWriteIOPS;
    }

    public void setDiskWriteIOPS(double diskWriteIOPS) {
        this.diskWriteIOPS = diskWriteIOPS;
    }

    public BigDecimal getVpcPublicIPInternetInRate() {
        return vpcPublicIPInternetInRate;
    }

    public void setVpcPublicIPInternetInRate(BigDecimal vpcPublicIPInternetInRate) {
        this.vpcPublicIPInternetInRate = vpcPublicIPInternetInRate;
    }

    public BigDecimal getVpcPublicIPInternetOutRate() {
        return vpcPublicIPInternetOutRate;
    }

    public void setVpcPublicIPInternetOutRate(BigDecimal vpcPublicIPInternetOutRate) {
        this.vpcPublicIPInternetOutRate = vpcPublicIPInternetOutRate;
    }

    public double getVpcPublicIPInternetOutRatePercent() {
        return vpcPublicIPInternetOutRatePercent;
    }

    public void setVpcPublicIPInternetOutRatePercent(double vpcPublicIPInternetOutRatePercent) {
        this.vpcPublicIPInternetOutRatePercent = vpcPublicIPInternetOutRatePercent;
    }

    public double getCpuIdle() {
        return cpuIdle;
    }

    public void setCpuIdle(double cpuIdle) {
        this.cpuIdle = cpuIdle;
    }

    public double getCpuSystem() {
        return cpuSystem;
    }

    public void setCpuSystem(double cpuSystem) {
        this.cpuSystem = cpuSystem;
    }

    public double getCpuUser() {
        return cpuUser;
    }

    public void setCpuUser(double cpuUser) {
        this.cpuUser = cpuUser;
    }

    public double getCpuWait() {
        return cpuWait;
    }

    public void setCpuWait(double cpuWait) {
        this.cpuWait = cpuWait;
    }

    public double getCpuOther() {
        return cpuOther;
    }

    public void setCpuOther(double cpuOther) {
        this.cpuOther = cpuOther;
    }

    public double getCpuTotal() {
        return cpuTotal;
    }

    public void setCpuTotal(double cpuTotal) {
        this.cpuTotal = cpuTotal;
    }

    public BigDecimal getMemoryTotalspace() {
        return memoryTotalspace;
    }

    public void setMemoryTotalspace(BigDecimal memoryTotalspace) {
        this.memoryTotalspace = memoryTotalspace;
    }

    public BigDecimal getMemoryUsedspace() {
        return memoryUsedspace;
    }

    public void setMemoryUsedspace(BigDecimal memoryUsedspace) {
        this.memoryUsedspace = memoryUsedspace;
    }

    public BigDecimal getMemoryActualusedspace() {
        return memoryActualusedspace;
    }

    public void setMemoryActualusedspace(BigDecimal memoryActualusedspace) {
        this.memoryActualusedspace = memoryActualusedspace;
    }

    public BigDecimal getMemoryFreespace() {
        return memoryFreespace;
    }

    public void setMemoryFreespace(BigDecimal memoryFreespace) {
        this.memoryFreespace = memoryFreespace;
    }

    public double getMemoryFreeutilization() {
        return memoryFreeutilization;
    }

    public void setMemoryFreeutilization(double memoryFreeutilization) {
        this.memoryFreeutilization = memoryFreeutilization;
    }

    public double getMemoryUsedutilization() {
        return memoryUsedutilization;
    }

    public void setMemoryUsedutilization(double memoryUsedutilization) {
        this.memoryUsedutilization = memoryUsedutilization;
    }

    public String getLoad1m() {
        return load1m;
    }

    public void setLoad1m(String load1m) {
        this.load1m = load1m;
    }

    public String getLoad5m() {
        return load5m;
    }

    public void setLoad5m(String load5m) {
        this.load5m = load5m;
    }

    public String getLoad15m() {
        return load15m;
    }

    public void setLoad15m(String load15m) {
        this.load15m = load15m;
    }

    public BigDecimal getDiskusageUsed() {
        return diskusageUsed;
    }

    public void setDiskusageUsed(BigDecimal diskusageUsed) {
        this.diskusageUsed = diskusageUsed;
    }

    public double getDiskusageUtilization() {
        return diskusageUtilization;
    }

    public void setDiskusageUtilization(double diskusageUtilization) {
        this.diskusageUtilization = diskusageUtilization;
    }

    public BigDecimal getDiskusageFree() {
        return diskusageFree;
    }

    public void setDiskusageFree(BigDecimal diskusageFree) {
        this.diskusageFree = diskusageFree;
    }

    public BigDecimal getDiskusageTotal() {
        return diskusageTotal;
    }

    public void setDiskusageTotal(BigDecimal diskusageTotal) {
        this.diskusageTotal = diskusageTotal;
    }

    public BigDecimal getDiskReadbytes() {
        return diskReadbytes;
    }

    public void setDiskReadbytes(BigDecimal diskReadbytes) {
        this.diskReadbytes = diskReadbytes;
    }

    public BigDecimal getDiskWritebytes() {
        return diskWritebytes;
    }

    public void setDiskWritebytes(BigDecimal diskWritebytes) {
        this.diskWritebytes = diskWritebytes;
    }

    public long getDiskReadiops() {
        return diskReadiops;
    }

    public void setDiskReadiops(long diskReadiops) {
        this.diskReadiops = diskReadiops;
    }

    public long getDiskWriteiops() {
        return diskWriteiops;
    }

    public void setDiskWriteiops(long diskWriteiops) {
        this.diskWriteiops = diskWriteiops;
    }

    public double getFsInodeutilization() {
        return fsInodeutilization;
    }

    public void setFsInodeutilization(double fsInodeutilization) {
        this.fsInodeutilization = fsInodeutilization;
    }

    public BigDecimal getNetworkinRate() {
        return networkinRate;
    }

    public void setNetworkinRate(BigDecimal networkinRate) {
        this.networkinRate = networkinRate;
    }

    public BigDecimal getNetworkoutRate() {
        return networkoutRate;
    }

    public void setNetworkoutRate(BigDecimal networkoutRate) {
        this.networkoutRate = networkoutRate;
    }

    public long getNetworkinPackages() {
        return networkinPackages;
    }

    public void setNetworkinPackages(long networkinPackages) {
        this.networkinPackages = networkinPackages;
    }

    public long getNetworkoutPackages() {
        return networkoutPackages;
    }

    public void setNetworkoutPackages(long networkoutPackages) {
        this.networkoutPackages = networkoutPackages;
    }

    public long getNetworkinErrorpackages() {
        return networkinErrorpackages;
    }

    public void setNetworkinErrorpackages(long networkinErrorpackages) {
        this.networkinErrorpackages = networkinErrorpackages;
    }

    public long getNetworkoutErrorpackages() {
        return networkoutErrorpackages;
    }

    public void setNetworkoutErrorpackages(long networkoutErrorpackages) {
        this.networkoutErrorpackages = networkoutErrorpackages;
    }

    public long getNetTcpconnection() {
        return netTcpconnection;
    }

    public void setNetTcpconnection(long netTcpconnection) {
        this.netTcpconnection = netTcpconnection;
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
}
