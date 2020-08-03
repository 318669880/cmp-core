package com.fit2cloud.commons.server.process.dto;

import java.util.List;

public class ProcessDTO {

    private List<ProcessDataDTO> data;

    private String processId;

    private String modelId;

    private String modelName;

    private String processName;

    private String creator;

    private String businessKey;

    private String businessType;

    private String workspaceId;

    private List<String> nextStepAssignees;

    public List<String> getNextStepAssignees() {
        return nextStepAssignees;
    }

    public void setNextStepAssignees(List<String> nextStepAssignees) {
        this.nextStepAssignees = nextStepAssignees;
    }

    public List<ProcessDataDTO> getData() {
        return data;
    }

    public void setData(List<ProcessDataDTO> data) {
        this.data = data;
    }

    public String getModelId() {
        return modelId;
    }

    public void setModelId(String modelId) {
        this.modelId = modelId;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getBusinessKey() {
        return businessKey;
    }

    public void setBusinessKey(String businessKey) {
        this.businessKey = businessKey;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public String getWorkspaceId() {
        return workspaceId;
    }

    public void setWorkspaceId(String workspaceId) {
        this.workspaceId = workspaceId;
    }
}
