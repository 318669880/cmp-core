package com.fit2cloud.commons.server.process;

import com.fit2cloud.commons.server.base.domain.FlowProcess;
import com.fit2cloud.commons.server.base.domain.FlowTask;

import java.util.List;

public class ProcessEventContext {

    private List<FlowTask> tasks;

    private FlowTask task;

    private FlowProcess process;

    private String arguments;

    public List<FlowTask> getTasks() {
        return tasks;
    }

    public void setTasks(List<FlowTask> tasks) {
        this.tasks = tasks;
    }

    public FlowTask getTask() {
        return task;
    }

    public void setTask(FlowTask task) {
        this.task = task;
    }

    public FlowProcess getProcess() {
        return process;
    }

    public void setProcess(FlowProcess process) {
        this.process = process;
    }

    public String getArguments() {
        return arguments;
    }

    public void setArguments(String arguments) {
        this.arguments = arguments;
    }
}
