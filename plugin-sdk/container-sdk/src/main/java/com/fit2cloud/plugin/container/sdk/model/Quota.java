package com.fit2cloud.plugin.container.sdk.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by liqiang on 2018/9/11.
 */
public class Quota extends BaseObject {

    private String status;

    private Map<String, String> hard = new HashMap<>();

    private List<String> scopes = new ArrayList<>();

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Map<String, String> getHard() {
        return hard;
    }

    public void setHard(Map<String, String> hard) {
        this.hard = hard;
    }

    public List<String> getScopes() {
        return scopes;
    }

    public void setScopes(List<String> scopes) {
        this.scopes = scopes;
    }
}
