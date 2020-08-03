package com.fit2cloud.commons.server.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Menu implements Serializable {

    private static final long serialVersionUID = -6122963050650664769L;
    //菜单排序
    private int order = 100;

    private String id;

    private String name;

    private String icon;

    private String title;

    private String url;

    private String templateUrl;

    private String open = "current";

    private List<MenuPermission> requiredPermissions = new ArrayList<>();

    private String moduleId;

    private boolean isTop = false;

    private List<Menu> children;

    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    public boolean isTop() {
        return isTop;
    }

    public void setTop(boolean top) {
        isTop = top;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOpen() {
        return open;
    }

    public void setOpen(String open) {
        this.open = open;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTemplateUrl() {
        return templateUrl;
    }

    public void setTemplateUrl(String templateUrl) {
        this.templateUrl = templateUrl;
    }

    public List<Menu> getChildren() {
        return children;
    }

    public void setChildren(List<Menu> children) {
        this.children = children;
    }

    public List<MenuPermission> getRequiredPermissions() {
        return requiredPermissions;
    }

    public void setRequiredPermissions(List<MenuPermission> requiredPermissions) {
        this.requiredPermissions = requiredPermissions;
    }
}
