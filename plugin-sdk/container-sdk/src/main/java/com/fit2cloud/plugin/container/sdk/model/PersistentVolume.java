package com.fit2cloud.plugin.container.sdk.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liqiang on 2018/9/11.
 */
public class PersistentVolume extends BaseObject {

    private String status;

    private List<String> accessModes = new ArrayList<>();

    private float capacity;

    private String type;

    private String claimRef;

    private String projectName;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<String> getAccessModes() {
        return accessModes;
    }

    public void setAccessModes(List<String> accessModes) {
        this.accessModes = accessModes;
    }

    public float getCapacity() {
        return capacity;
    }

    public void setCapacity(float capacity) {
        this.capacity = capacity;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getClaimRef() {
        return claimRef;
    }

    public void setClaimRef(String claimRef) {
        this.claimRef = claimRef;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
}
