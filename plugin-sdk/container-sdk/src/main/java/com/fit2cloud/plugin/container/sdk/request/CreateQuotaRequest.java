package com.fit2cloud.plugin.container.sdk.request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateQuotaRequest extends BaseRequest {

    private String projectName;

    private String name;

    private Map<String, String> hard = new HashMap<>();

    private List<String> scopes = new ArrayList<>();

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

    public Map<String, String> getHard() {
        return hard;
    }

    public void setHard(Map<String, String> hard) {
        this.hard = hard;
    }

    public List<String> getScopes() {
        return scopes;
    }

    public void setScopes(List<String> scopes) {
        this.scopes = scopes;
    }
}
