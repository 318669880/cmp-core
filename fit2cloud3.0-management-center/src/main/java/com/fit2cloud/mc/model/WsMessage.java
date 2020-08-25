package com.fit2cloud.mc.model;

import java.io.Serializable;

public class WsMessage<T> implements Serializable {
    private String userId;

    private String topic;

    private T data;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public WsMessage() {
    }

    public WsMessage(String userId, String topic, T data) {
        this.userId = userId;
        this.topic = topic;
        this.data = data;
    }
}
