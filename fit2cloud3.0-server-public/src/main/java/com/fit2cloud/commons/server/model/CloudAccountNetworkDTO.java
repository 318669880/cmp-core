package com.fit2cloud.commons.server.model;

import io.swagger.annotations.ApiModelProperty;

public class CloudAccountNetworkDTO extends CloudAccountDTO {

    @ApiModelProperty("状态")
    private String networkSyncStatus;

    @ApiModelProperty("同步时间")
    private Long syncTime;

    private String settings;

    public String getNetworkSyncStatus() {
        return networkSyncStatus;
    }

    public void setNetworkSyncStatus(String networkSyncStatus) {
        this.networkSyncStatus = networkSyncStatus;
    }

    public Long getSyncTime() {
        return syncTime;
    }

    public void setSyncTime(Long syncTime) {
        this.syncTime = syncTime;
    }

    public String getSettings() {
        return settings;
    }

    public void setSettings(String settings) {
        this.settings = settings;
    }
}
