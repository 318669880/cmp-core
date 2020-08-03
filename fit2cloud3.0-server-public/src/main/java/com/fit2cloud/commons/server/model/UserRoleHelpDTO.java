package com.fit2cloud.commons.server.model;

public class UserRoleHelpDTO {

    private String roleId;
    private String roleName;
    private String roleParentId;

    private String sourceId;
    private String sourceName;

    private String parentId;

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleParentId() {
        return roleParentId;
    }

    public void setRoleParentId(String roleParentId) {
        this.roleParentId = roleParentId;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }
}
