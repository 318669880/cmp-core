package com.fit2cloud.commons.server.model;

public class ScriptRunRequest {

    private String os;
    private String content;
    private String manageIp;
    private String username;
    private String password;



    public ScriptRunRequest build() {
        return this;
    }

    public ScriptRunRequest withOs(String os) {
        this.os = os;
        return this;
    }

    public ScriptRunRequest withContent(String content) {
        this.content = content;
        return this;
    }

    public ScriptRunRequest withManageIp(String manageIp) {
        this.manageIp = manageIp;
        return this;
    }

    public ScriptRunRequest withUsername(String username) {
        this.username = username;
        return this;
    }

    public ScriptRunRequest withPassword(String password) {
        this.password = password;
        return this;
    }

    public String getOs() {
        return os;
    }

    public String getContent() {
        return content;
    }

    public String getManageIp() {
        return manageIp;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
