package com.fit2cloud.commons.server.model.request;

import io.swagger.annotations.ApiModelProperty;

public class CloudClusterRequest {
    @ApiModelProperty("云账号ID")
    private String accountId;

    @ApiModelProperty("数据中心")
    private String region;

    @ApiModelProperty("集群名称")
    private String name;

    @ApiModelProperty("宿主机数量最小值")
    private String hostCountStart;

    @ApiModelProperty("宿主机数量最大值")
    private String hostCountEnd;

    @ApiModelProperty("CPU总容量最小值")
    private String cpuTotalStart;

    @ApiModelProperty("CPU总容量最大值")
    private String cpuTotalEnd;

    @ApiModelProperty("内存总容量最小值")
    private String memoryTotalStart;

    @ApiModelProperty("内存总容量最大值")
    private String memoryTotalEnd;

    @ApiModelProperty("运行中的虚拟机数量最小值")
    private String runningCountStart;

    @ApiModelProperty("运行中的虚拟机数量最大值")
    private String runningCountEnd;

    @ApiModelProperty("排序key")
    private String sort;

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHostCountStart() {
        return hostCountStart;
    }

    public void setHostCountStart(String hostCountStart) {
        this.hostCountStart = hostCountStart;
    }

    public String getHostCountEnd() {
        return hostCountEnd;
    }

    public void setHostCountEnd(String hostCountEnd) {
        this.hostCountEnd = hostCountEnd;
    }

    public String getCpuTotalStart() {
        return cpuTotalStart;
    }

    public void setCpuTotalStart(String cpuTotalStart) {
        this.cpuTotalStart = cpuTotalStart;
    }

    public String getCpuTotalEnd() {
        return cpuTotalEnd;
    }

    public void setCpuTotalEnd(String cpuTotalEnd) {
        this.cpuTotalEnd = cpuTotalEnd;
    }

    public String getMemoryTotalStart() {
        return memoryTotalStart;
    }

    public void setMemoryTotalStart(String memoryTotalStart) {
        this.memoryTotalStart = memoryTotalStart;
    }

    public String getMemoryTotalEnd() {
        return memoryTotalEnd;
    }

    public void setMemoryTotalEnd(String memoryTotalEnd) {
        this.memoryTotalEnd = memoryTotalEnd;
    }

    public String getRunningCountStart() {
        return runningCountStart;
    }

    public void setRunningCountStart(String runningCountStart) {
        this.runningCountStart = runningCountStart;
    }

    public String getRunningCountEnd() {
        return runningCountEnd;
    }

    public void setRunningCountEnd(String runningCountEnd) {
        this.runningCountEnd = runningCountEnd;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }
}
