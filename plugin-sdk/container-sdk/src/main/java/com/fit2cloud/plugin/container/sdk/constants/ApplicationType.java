package com.fit2cloud.plugin.container.sdk.constants;

/**
 * Created by liqiang on 2018/9/11.
 */
public enum ApplicationType {

    STATEFUL_SET("StatefulSet"), DEPLOYMENT("Deployment"), DEPLOYMENT_CONFIG("DeploymentConfig"), REPLICATION_CONTROLLER("ReplicationController");

    private String value;

    ApplicationType(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
