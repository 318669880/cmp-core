package com.fit2cloud.commons.server.model.billing;

import java.io.Serializable;

/**
 * Created by liqiang on 2018/12/11.
 */
public class BillingResource implements Serializable {

    private String id;

    private String resourceId;

    private String resourceName;

    private String resourceType;

    private int cpu;

    private int mem;

    private int storage;

    private String instanceType;

    private long resourceCreateTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public int getCpu() {
        return cpu;
    }

    public void setCpu(int cpu) {
        this.cpu = cpu;
    }

    public int getMem() {
        return mem;
    }

    public void setMem(int mem) {
        this.mem = mem;
    }

    public int getStorage() {
        return storage;
    }

    public void setStorage(int storage) {
        this.storage = storage;
    }

    public String getInstanceType() {
        return instanceType;
    }

    public void setInstanceType(String instanceType) {
        this.instanceType = instanceType;
    }

    public long getResourceCreateTime() {
        return resourceCreateTime;
    }

    public void setResourceCreateTime(long resourceCreateTime) {
        this.resourceCreateTime = resourceCreateTime;
    }
}
