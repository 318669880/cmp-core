package com.fit2cloud.commons.server.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Permission implements Serializable {

    private static final long serialVersionUID = -2804942334634034019L;

    private String id;

    private String name;

    private String resourceId;

    private List<String> parentRoles = new ArrayList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public List<String> getParentRoles() {
        return parentRoles;
    }

    public void setParentRoles(List<String> parentRoles) {
        this.parentRoles = parentRoles;
    }
}
