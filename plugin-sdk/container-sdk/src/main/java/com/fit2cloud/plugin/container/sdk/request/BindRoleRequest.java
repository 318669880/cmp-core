package com.fit2cloud.plugin.container.sdk.request;

import com.fit2cloud.plugin.container.sdk.model.Role;
import com.fit2cloud.plugin.container.sdk.model.Subject;

import java.util.List;

public class BindRoleRequest extends BaseRequest {

    private String projectName;

    private Subject subject;

    private List<Role> roles;

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }
}
