package com.fit2cloud.mc.common.constants;

public enum BusinessCacheConstants {

    MODEL_MANAGER_INFO("model-manager-info"),
    MODEL_BASIC_LISTS("model-basic-lists"),
    K8S_POD_CACHE("k8s-pod-cache"),
    HOST_NODES_CACHE("host-nodes-cache");

    private String cacheName;

    BusinessCacheConstants(String cacheName) {
        this.cacheName = cacheName;
    }

    public String getCacheName() {
        return cacheName;
    }
}
