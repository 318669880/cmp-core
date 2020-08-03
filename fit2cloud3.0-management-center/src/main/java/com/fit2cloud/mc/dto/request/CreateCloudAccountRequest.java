package com.fit2cloud.mc.dto.request;

import io.swagger.annotations.ApiModelProperty;

public class CreateCloudAccountRequest {

    @ApiModelProperty(value = "名称", required = true)
    private String name;
    @ApiModelProperty(value = "插件名称", required = true)
    private String pluginName;

    @ApiModelProperty(value = "凭据", required = true)
    private String credential;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPluginName() {
        return pluginName;
    }

    public void setPluginName(String pluginName) {
        this.pluginName = pluginName;
    }

    public String getCredential() {
        return credential;
    }

    public void setCredential(String credential) {
        this.credential = credential;
    }
}
