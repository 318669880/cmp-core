package com.fit2cloud.plugin.container.sdk.model;

import java.util.ArrayList;
import java.util.List;

public class Group extends BaseObject {

    private List<String> users = new ArrayList<>();

    public List<String> getUsers() {
        return users;
    }

    public void setUsers(List<String> users) {
        this.users = users;
    }
}
