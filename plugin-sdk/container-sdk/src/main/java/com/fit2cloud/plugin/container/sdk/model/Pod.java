package com.fit2cloud.plugin.container.sdk.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liqiang on 2018/9/11.
 */
public class Pod extends BaseObject {

    private String applicationId;

    private String nodeName;

    private String hostIp;

    private String podIp;

    private String state;

    private boolean ready;

    private String restartPolicy;

    private String volumes;

    private List<Container> containers = new ArrayList<>();

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public String getHostIp() {
        return hostIp;
    }

    public void setHostIp(String hostIp) {
        this.hostIp = hostIp;
    }

    public String getPodIp() {
        return podIp;
    }

    public void setPodIp(String podIp) {
        this.podIp = podIp;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public String getRestartPolicy() {
        return restartPolicy;
    }

    public void setRestartPolicy(String restartPolicy) {
        this.restartPolicy = restartPolicy;
    }

    public String getVolumes() {
        return volumes;
    }

    public void setVolumes(String volumes) {
        this.volumes = volumes;
    }

    public List<Container> getContainers() {
        return containers;
    }

    public void addContainer(Container container) {
        containers.add(container);
    }

    @Override
    public void setProjectId(String projectId) {
        containers.forEach(container -> container.setProjectId(projectId));
        super.setProjectId(projectId);
    }
}
