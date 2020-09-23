package com.fit2cloud.sdk.model;

public class ResetHostnameRequest extends ExecuteScriptRequest {
    public static final String HOSTNAME_PLACEHOLER = "@\\[HOSTNAME\\]";
    private String hostname;

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }
}
