package com.fit2cloud.plugin.container.sdk.model;

/**
 * Created by liqiang on 2018/9/11.
 */
public class Role extends BaseObject {

    public Role() {
    }

    public Role(String name, String kind) {
        setName(name);
        setKind(kind);
    }

    private boolean systemOnly;

    private String description;

    private String rules;

    public boolean isSystemOnly() {
        return systemOnly;
    }

    public void setSystemOnly(boolean systemOnly) {
        this.systemOnly = systemOnly;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRules() {
        return rules;
    }

    public void setRules(String rules) {
        this.rules = rules;
    }
}
