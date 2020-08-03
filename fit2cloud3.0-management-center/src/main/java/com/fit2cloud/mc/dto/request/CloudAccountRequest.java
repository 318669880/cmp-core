package com.fit2cloud.mc.dto.request;

import com.fit2cloud.commons.annotation.FuzzyQuery;
import io.swagger.annotations.ApiModelProperty;

public class CloudAccountRequest {

    @ApiModelProperty("ID")
    private String id;

    @ApiModelProperty(value = "云账号名称,模糊匹配")
    @FuzzyQuery
    private String name;

    @ApiModelProperty("插件名称")
    private String pluginName;

    @ApiModelProperty(value = "插件类型", allowableValues = "infrastructure,container")
    private String pluginType;

    @ApiModelProperty(value = "状态", allowableValues = "VALID,INVALID,DELETED")
    private String status;

    @ApiModelProperty(value = "同步状态", allowableValues = "pending,success,error,sync")
    private String syncStatus;

    @ApiModelProperty(value = "排序key", hidden = true)
    private String sort;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public String getPluginType() {
        return pluginType;
    }

    public void setPluginType(String pluginType) {
        this.pluginType = pluginType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSyncStatus() {
        return syncStatus;
    }

    public void setSyncStatus(String syncStatus) {
        this.syncStatus = syncStatus;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }
}
