package com.fit2cloud.sdk.model;

/**
 * 查询磁盘请求实体
 */
public class VmDisksRequest extends Request {
    private String instanceId;
    private String diskId;
    private String projectId;

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public String getDiskId() {
        return diskId;
    }

    public void setDiskId(String diskId) {
        this.diskId = diskId;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }
}
