package com.fit2cloud.plugin.container.sdk.model;

/**
 * Created by liqiang on 2018/9/11.
 */
public class Project extends BaseObject {

    private String displayName;

    private String description;

    private String workspaceId;

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getWorkspaceId() {
        return workspaceId;
    }

    public void setWorkspaceId(String workspaceId) {
        this.workspaceId = workspaceId;
    }
}
