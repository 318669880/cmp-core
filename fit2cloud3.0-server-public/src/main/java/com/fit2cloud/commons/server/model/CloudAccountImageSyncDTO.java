package com.fit2cloud.commons.server.model;

import com.fit2cloud.commons.server.base.domain.CloudAccount;
import io.swagger.annotations.ApiModelProperty;

public class CloudAccountImageSyncDTO extends CloudAccountDTO {

    @ApiModelProperty("内容")
    private String details;

    @ApiModelProperty("设置时间")
    private Long settingTime;

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public Long getSettingTime() {
        return settingTime;
    }

    public void setSettingTime(Long settingTime) {
        this.settingTime = settingTime;
    }
}
