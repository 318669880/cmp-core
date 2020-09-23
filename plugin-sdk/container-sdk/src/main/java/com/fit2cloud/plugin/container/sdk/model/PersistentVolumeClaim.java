package com.fit2cloud.plugin.container.sdk.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liqiang on 2018/9/11.
 */
public class PersistentVolumeClaim extends BaseObject {

    private String status;

    private float capacity;

    private String volume;

    private List<String> accessModes = new ArrayList<>();

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public float getCapacity() {
        return capacity;
    }

    public void setCapacity(float capacity) {
        this.capacity = capacity;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public List<String> getAccessModes() {
        return accessModes;
    }

    public void setAccessModes(List<String> accessModes) {
        this.accessModes = accessModes;
    }
}
