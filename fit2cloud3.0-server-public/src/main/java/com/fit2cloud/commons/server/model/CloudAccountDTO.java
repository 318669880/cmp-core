package com.fit2cloud.commons.server.model;

import com.fit2cloud.commons.server.base.domain.CloudAccount;
import io.swagger.annotations.ApiModelProperty;

public class CloudAccountDTO extends CloudAccount {

    @ApiModelProperty("图标")
    private String icon;

    @ApiModelProperty("插件描述")
    private String pluginDesc;

    @ApiModelProperty("插件类型")
    private String pluginType;

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getPluginDesc() {
        return pluginDesc;
    }

    public void setPluginDesc(String pluginDesc) {
        this.pluginDesc = pluginDesc;
    }

    public String getPluginType() {
        return pluginType;
    }

    public void setPluginType(String pluginType) {
        this.pluginType = pluginType;
    }
}
