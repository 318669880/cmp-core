package com.fit2cloud.commons.server.model;


import com.fit2cloud.commons.server.base.domain.CloudServer;
import com.fit2cloud.commons.server.base.domain.CloudServerCredential;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

public class CloudServerDTO extends CloudServer {
    @ApiModelProperty("组织名称")
    private String organizationName;
    @ApiModelProperty("工作空间名称")
    private String workspaceName;
    @ApiModelProperty(hidden = true)
    private String icon;
    @ApiModelProperty(hidden = true)
    private String cloudProviderName;
    @ApiModelProperty("插件名称")
    private String cloudProviderZHName;
    @ApiModelProperty("云账号名称")
    private String accountName;
    @ApiModelProperty("CPU平均使用率")
    private String cpuUsagePercent; //用于放置优化建议中从Prometheus查到的CPU平均使用率
    @ApiModelProperty("内存平均使用率")
    private String memoryUsagePercent; //同上
    @ApiModelProperty("CPU最大使用率")
    private String cpuMaxUsagePercent;
    @ApiModelProperty("内存平均使用率")
    private String memoryMaxUsagePercent;
    @ApiModelProperty("内存平均使用率")
    private String applyName;


    public String getCpuMaxUsagePercent() {
        return cpuMaxUsagePercent;
    }

    public void setCpuMaxUsagePercent(String cpuMaxUsagePercent) {
        this.cpuMaxUsagePercent = cpuMaxUsagePercent;
    }

    public String getMemoryMaxUsagePercent() {
        return memoryMaxUsagePercent;
    }

    public void setMemoryMaxUsagePercent(String memoryMaxUsagePercent) {
        this.memoryMaxUsagePercent = memoryMaxUsagePercent;
    }

    private List<CloudServerCredential> credentialList;

    public List<CloudServerCredential> getCredentialList() {
        return credentialList;
    }

    public void setCredentialList(List<CloudServerCredential> credentialList) {
        this.credentialList = credentialList;
    }

    public String getCloudProviderName() {
        return cloudProviderName;
    }

    public void setCloudProviderName(String cloudProviderName) {
        this.cloudProviderName = cloudProviderName;
    }

    public String getCloudProviderZHName() {
        return cloudProviderZHName;
    }

    public void setCloudProviderZHName(String cloudProviderZHName) {
        this.cloudProviderZHName = cloudProviderZHName;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getWorkspaceName() {
        return workspaceName;
    }

    public void setWorkspaceName(String workspaceName) {
        this.workspaceName = workspaceName;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getCpuUsagePercent() {
        return cpuUsagePercent;
    }

    public void setCpuUsagePercent(String cpuUsagePercent) {
        this.cpuUsagePercent = cpuUsagePercent;
    }

    public String getMemoryUsagePercent() {
        return memoryUsagePercent;
    }

    public void setMemoryUsagePercent(String memoryUsagePercent) {
        this.memoryUsagePercent = memoryUsagePercent;
    }

    public String getApplyName() {
        return applyName;
    }

    public void setApplyName(String applyName) {
        this.applyName = applyName;
    }
}
