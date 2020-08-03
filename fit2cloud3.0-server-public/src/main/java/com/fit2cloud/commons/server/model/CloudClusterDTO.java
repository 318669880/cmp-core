package com.fit2cloud.commons.server.model;

import com.fit2cloud.commons.server.base.domain.CloudCluster;
import io.swagger.annotations.ApiModelProperty;

public class CloudClusterDTO extends CloudCluster {
    @ApiModelProperty("云账号名称")
    private String accountName;
    @ApiModelProperty(hidden = true)
    private String icon;

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
