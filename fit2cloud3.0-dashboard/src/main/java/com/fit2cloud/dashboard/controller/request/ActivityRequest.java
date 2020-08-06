package com.fit2cloud.dashboard.controller.request;

import com.fit2cloud.commons.annotation.FuzzyQuery;
import io.swagger.annotations.ApiModelProperty;

public class ActivityRequest {

    @ApiModelProperty("模块ID")
    private String module;

    @ApiModelProperty("资源名称,模糊匹配")
    @FuzzyQuery
    private String resourceName;

    @ApiModelProperty("操作人,ID、邮箱、名称模糊匹配")
    @FuzzyQuery
    private String username;

    @ApiModelProperty("操作模糊匹配")
    @FuzzyQuery
    private String operation;

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }
}
