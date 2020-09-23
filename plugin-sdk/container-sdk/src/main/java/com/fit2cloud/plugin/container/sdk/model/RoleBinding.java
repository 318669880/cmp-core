package com.fit2cloud.plugin.container.sdk.model;

/**
 * Created by liqiang on 2018/9/11.
 */
public class RoleBinding extends BaseObject {

    private String subjectName;

    private String subjectKind;

    private String roleName;

    private String roleKind;

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getSubjectKind() {
        return subjectKind;
    }

    public void setSubjectKind(String subjectKind) {
        this.subjectKind = subjectKind;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleKind() {
        return roleKind;
    }

    public void setRoleKind(String roleKind) {
        this.roleKind = roleKind;
    }
}
