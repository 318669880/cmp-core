package com.fit2cloud.plugin.container.sdk.request;

import com.fit2cloud.plugin.container.sdk.model.Application;

import java.util.List;

public class GetServiceRequest extends BaseRequest {

    private String projectId;

    private String projectName;

    private List<Application> applications;

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public List<Application> getApplications() {
        return applications;
    }

    public void setApplications(List<Application> applications) {
        this.applications = applications;
    }
}
