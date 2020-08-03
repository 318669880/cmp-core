package com.fit2cloud.commons.server.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TreeNode implements Serializable {

    private String id;
    private String name;
    private int order = 0;

    List<TreeNode> children = new ArrayList<>();

    private boolean checked = false;

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public TreeNode() {

    }

    public TreeNode(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public TreeNode(String id, String name, int order) {
        this.id = id;
        this.name = name;
        this.order = order;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

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

    public List<TreeNode> getChildren() {
        return children;
    }

    public void setChildren(List<TreeNode> children) {
        this.children = children;
    }
}
