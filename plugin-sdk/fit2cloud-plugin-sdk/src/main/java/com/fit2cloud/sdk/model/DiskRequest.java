package com.fit2cloud.sdk.model;

import java.util.List;

public class DiskRequest extends Request {
    //虚拟机id
    private String resourceId;
    private List<F2CDisk> disks;
    //卸载磁盘时是否删除磁盘
    private boolean destroy;
    private String projectId;

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public List<F2CDisk> getDisks() {
        return disks;
    }

    public void setDisks(List<F2CDisk> disks) {
        this.disks = disks;
    }

    public boolean isDestroy() {
        return destroy;
    }

    public void setDestroy(boolean destroy) {
        this.destroy = destroy;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }
}
