package com.fit2cloud.commons.server.model;

import com.fit2cloud.commons.server.base.domain.Module;

import java.util.List;
import java.util.Set;

public class ModuleDTO extends Module {

    private List<Menu> menus;

    private Set<String> permissions;

    public List<Menu> getMenus() {
        return menus;
    }

    public void setMenus(List<Menu> menus) {
        this.menus = menus;
    }

    public Set<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<String> permissions) {
        this.permissions = permissions;
    }
}
