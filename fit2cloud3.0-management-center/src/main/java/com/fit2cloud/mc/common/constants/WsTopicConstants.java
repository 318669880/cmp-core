package com.fit2cloud.mc.common.constants;

import java.util.HashMap;
import java.util.Map;

/**
 * @Company: FIT2CLOUD 飞致云
 * @Author: Mr.cyw
 * @Machine: chenyawen
 * @Date: 2020/11/11 11:45 上午
 * @Description: Please Write notes scientifically
 */
public enum WsTopicConstants {

    /*model_node_fresh_topic("model_node_fresh_topic"),
    model_k8s_uninstall_topic("model_k8s_uninstall_topic");*/

    HOST_NODE_START("/topic/host/start"),
    HOST_NODE_STOP("/topic/host/stop"),
    HOST_NODE_INSTALL("/topic/host/install"),
    K8S_MODEL_INSTALL("/topic/k8s/install"),
    K8S_MODEL_START("/topic/k8s/start"),
    K8S_MODEL_STOP("/topic/k8s/stop"),
    K8S_MODEL_UNINSTALL("/topic/k8s/uninstall");
    private String value;

    WsTopicConstants(java.lang.String value) {
        this.value = value;
    }

    public Map<String, String> toMap(){
        Map<String,String> result = new HashMap<>();
        result.put("name", name());
        result.put("value", value());
        return result;
    }

    public String value() {
        return value;
    }
}
