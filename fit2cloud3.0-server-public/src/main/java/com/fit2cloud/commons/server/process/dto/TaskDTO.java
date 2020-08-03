package com.fit2cloud.commons.server.process.dto;

import com.fit2cloud.commons.server.base.domain.FlowTask;

public class TaskDTO extends FlowTask {

    private String processName;

    private String processCreator;

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public String getProcessCreator() {
        return processCreator;
    }

    public void setProcessCreator(String processCreator) {
        this.processCreator = processCreator;
    }
}
