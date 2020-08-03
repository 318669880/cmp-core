package com.fit2cloud.commons.server.process.request;

import com.fit2cloud.commons.server.process.dto.ProcessDataDTO;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

public class CompleteTaskRequest {

    @ApiModelProperty(value = "任务ID", required = true)
    private String taskId;

    @ApiModelProperty(value = "审批备注")
    private String remarks;

    @ApiModelProperty(value = "指定审批人")
    private List<String> nextStepAssignees;

    @ApiModelProperty(value = "流程变量")
    private List<ProcessDataDTO> data;

    public List<String> getNextStepAssignees() {
        return nextStepAssignees;
    }

    public void setNextStepAssignees(List<String> nextStepAssignees) {
        this.nextStepAssignees = nextStepAssignees;
    }

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

    public List<ProcessDataDTO> getData() {
        return data;
    }

    public void setData(List<ProcessDataDTO> data) {
        this.data = data;
    }
}
