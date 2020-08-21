package com.fit2cloud.mc.dto.request;

import java.util.List;
import java.util.Map;

public class OperatorModuleRequest {
    private List<String> modules ;
    private Map<String, Object> params;


    public List<String> getModules() {
        return modules;
    }

    public void setModules(List<String> modules) {
        this.modules = modules;
    }



    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }



}
