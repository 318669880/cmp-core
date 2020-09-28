package com.fit2cloud.commons.server.kobe;


import java.util.HashMap;
import java.util.Map;

/**
 * @Author gin
 * @Date 2020/8/25 4:33 下午
 */
public class AdhocRequest {
    private String ip;
    private Integer port;
    private String username;
    private String credential;
    private String content;
    private String header;
    private String module = "shell";
    private Long timeout = 300L;
    private String proxy = null;
    private Boolean become = false;
    private String becomeMethod = "su";
    private Map<String, String> vars = new HashMap();
    private String executePath;
    private CloudServerCredentialType cloudServerCredentailType;

    public AdhocRequest() {
    }

    public String getProxy() {
        return this.proxy;
    }

    public Map<String, String> getVars() {
        return this.vars;
    }

    public String getExecutePath() {
        return this.executePath;
    }

    public AdhocRequest withIp(String ip) {
        this.ip = ip;
        return this;
    }

    public AdhocRequest withBecome(Boolean become) {
        this.become = become;
        return this;
    }

    public AdhocRequest withTimeout(Long timeout) {
        this.timeout = timeout;
        return this;
    }

    public AdhocRequest withBecomeMethod(String method) {
        this.become = true;
        this.becomeMethod = method;
        return this;
    }

    public AdhocRequest withExecutePath(String executePath) {
        this.executePath = executePath;
        return this;
    }

    public AdhocRequest withPort(Integer port) {
        this.port = port;
        return this;
    }

    public AdhocRequest withUsername(String username) {
        this.username = username;
        return this;
    }

    public AdhocRequest withCredential(String credential) {
        this.credential = credential;
        return this;
    }

    public AdhocRequest withContent(String content) {
        this.content = content;
        return this;
    }

    public AdhocRequest withHeader(String header) {
        this.header = header;
        return this;
    }

    public AdhocRequest withModule(String module) {
        this.module = module;
        return this;
    }

    public void setProxy(String ip, Integer port, String username, String password) {
        StringBuffer buffer = new StringBuffer();
//        buffer.append("-o ProxyCommand=\"sshpass -p ").append(password).append(" ssh -W %h:%p -q ").append(username).append("@").append(ip).append("\" -o Port=").append(port).append(" -o StrictHostKeyChecking=no");
        buffer.append("{\"ip\":\"").append(ip).append("\",\"port\":").append(port).append(",\"username\":\"").append(username).append("\",\"password\":\"").append(password).append("\"}");
        this.proxy = buffer.toString();
    }

    public void setBecome(String username) {
    }

    public AdhocRequest withCredentialType(CloudServerCredentialType cloudServerCredentailType) {
        this.cloudServerCredentailType = cloudServerCredentailType;
        return this;
    }

    public AdhocRequest addVars(Map<String, String> vars) {
        this.vars.putAll(vars);
        return this;
    }

    public AdhocRequest addVar(String name, String value) {
        this.vars.put(name, value);
        return this;
    }

    public String getIp() {
        return this.ip;
    }

    public Integer getPort() {
        return this.port;
    }

    public String getUsername() {
        return this.username;
    }

    public String getCredential() {
        return this.credential;
    }

    public String getContent() {
        return this.content;
    }

    public CloudServerCredentialType getCloudServerCredentailType() {
        return this.cloudServerCredentailType;
    }

    public String getHeader() {
        return this.header;
    }

    public String getModule() {
        return this.module;
    }

    public Boolean getBecome() {
        return this.become;
    }

    public String getBecomeMethod() {
        return this.becomeMethod;
    }

    public Long getTimeout() {
        return this.timeout;
    }
}
