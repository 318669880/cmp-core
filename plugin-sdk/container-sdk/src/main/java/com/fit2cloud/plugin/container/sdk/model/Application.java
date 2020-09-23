package com.fit2cloud.plugin.container.sdk.model;

/**
 * Created by liqiang on 2018/9/11.
 */
public class Application extends BaseObject {

    private String app;

    private int availableReplicas;

    private int replicas;

    private LabelSelector labelSelector;

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public int getAvailableReplicas() {
        return availableReplicas;
    }

    public void setAvailableReplicas(int availableReplicas) {
        this.availableReplicas = availableReplicas;
    }

    public int getReplicas() {
        return replicas;
    }

    public void setReplicas(int replicas) {
        this.replicas = replicas;
    }

    public LabelSelector getLabelSelector() {
        return labelSelector;
    }

    public void setLabelSelector(LabelSelector labelSelector) {
        this.labelSelector = labelSelector;
    }
}
