package com.fit2cloud.mc.dto;

/**
 * Author: chunxing
 * Date: 2018/8/3  上午11:44
 * Description:
 */
public class McCard {

    private long workspaceCount;
    private long organizationCount;
    private long userCount;
    private long cloudAccountCount;

    public McCard() {
    }

    public McCard(long workspaceCount, long organizationCount, long userCount, long cloudAccountCount) {
        this.workspaceCount = workspaceCount;
        this.organizationCount = organizationCount;
        this.userCount = userCount;
        this.cloudAccountCount = cloudAccountCount;
    }

    public long getWorkspaceCount() {
        return workspaceCount;
    }

    public void setWorkspaceCount(long workspaceCount) {
        this.workspaceCount = workspaceCount;
    }

    public long getOrganizationCount() {
        return organizationCount;
    }

    public void setOrganizationCount(long organizationCount) {
        this.organizationCount = organizationCount;
    }

    public long getUserCount() {
        return userCount;
    }

    public void setUserCount(long userCount) {
        this.userCount = userCount;
    }

    public long getCloudAccountCount() {
        return cloudAccountCount;
    }

    public void setCloudAccountCount(long cloudAccountCount) {
        this.cloudAccountCount = cloudAccountCount;
    }

}
