package com.fit2cloud.commons.server.process.dto;

public class EventExecutor {
    private String key;
    private String value;

    public EventExecutor() {

    }

    public EventExecutor(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
