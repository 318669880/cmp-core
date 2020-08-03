package com.fit2cloud.commons.server.quartz;

import java.io.Serializable;

public class TaskParam implements Serializable {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
