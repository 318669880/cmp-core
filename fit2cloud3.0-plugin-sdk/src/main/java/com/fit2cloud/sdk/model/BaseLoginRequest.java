package com.fit2cloud.sdk.model;

public class BaseLoginRequest extends Request {
    private String resourceId;
    private String imageLoginPassword;
    private String ip;
    private String loginIp;
    private String loginUser;
    private String loginPassword;
    private int loginPort;
    private String ipv6Ip;

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getImageLoginPassword() {
        return imageLoginPassword;
    }

    public void setImageLoginPassword(String imageLoginPassword) {
        this.imageLoginPassword = imageLoginPassword;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getLoginIp() {
        return loginIp;
    }

    public void setLoginIp(String loginIp) {
        this.loginIp = loginIp;
    }

    public String getLoginUser() {
        return loginUser;
    }

    public void setLoginUser(String loginUser) {
        this.loginUser = loginUser;
    }

    public String getLoginPassword() {
        return loginPassword;
    }

    public void setLoginPassword(String loginPassword) {
        this.loginPassword = loginPassword;
    }

    public int getLoginPort() {
        return loginPort;
    }

    public void setLoginPort(int loginPort) {
        this.loginPort = loginPort;
    }

    public String getIpv6Ip() {
        return ipv6Ip;
    }

    public void setIpv6Ip(String ipv6Ip) {
        this.ipv6Ip = ipv6Ip;
    }
}
