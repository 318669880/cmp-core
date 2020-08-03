package com.fit2cloud.commons.server.process.request;

import io.swagger.annotations.ApiModelProperty;

public class RejectTaskRequest {

    @ApiModelProperty(value = "任务ID", required = true)
    private String taskId;

    @ApiModelProperty(value = "审批备注")
    private String remarks;

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
