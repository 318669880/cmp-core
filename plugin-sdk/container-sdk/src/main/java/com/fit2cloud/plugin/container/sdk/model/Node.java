package com.fit2cloud.plugin.container.sdk.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liqiang on 2018/9/11.
 */
public class Node extends BaseObject {

    private String hostname;

    private int cpu;

    private float memory;

    private int pod;

    private List<String> ip = new ArrayList<>();

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public int getCpu() {
        return cpu;
    }

    public void setCpu(int cpu) {
        this.cpu = cpu;
    }

    public float getMemory() {
        return memory;
    }

    public void setMemory(float memory) {
        this.memory = memory;
    }

    public int getPod() {
        return pod;
    }

    public void setPod(int pod) {
        this.pod = pod;
    }

    public List<String> getIp() {
        return ip;
    }
}
