package com.fit2cloud.commons.server.model;

public class UserTooltip {

    private String id;
    private String name;
    private String email;

    private boolean exist = true;

    public UserTooltip() {

    }

    public UserTooltip(String id, boolean exist) {
        this.id = id;
        this.exist = exist;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isExist() {
        return exist;
    }

    public void setExist(boolean exist) {
        this.exist = exist;
    }
}
