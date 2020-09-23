package com.fit2cloud.plugin.container.sdk.request;

public class DeleteQuotaRequest extends BaseRequest {

    private String projectName;

    private String name;

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
