package com.fit2cloud.plugin.container.sdk.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by liqiang on 2018/9/11.
 */
public class Service extends BaseObject {

    private String applicationId;

    private String app;

    private String clusterIp;

    private Map<String, String> selector = new HashMap<>();

    private List<ServicePort> servicePorts = new ArrayList<>();

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public String getClusterIp() {
        return clusterIp;
    }

    public void setClusterIp(String clusterIp) {
        this.clusterIp = clusterIp;
    }

    public Map<String, String> getSelector() {
        return selector;
    }

    public void putSelector(Map<String, String> selector) {
        if (selector != null) {
            this.selector.putAll(selector);
        }
    }

    public List<ServicePort> getServicePorts() {
        return servicePorts;
    }

    public void addServicePort(ServicePort servicePort) {
        servicePort.setProjectId(getProjectId());
        servicePort.setServiceId(getId());
        servicePort.setServiceName(getName());
        this.servicePorts.add(servicePort);
    }
}
