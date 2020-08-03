package com.fit2cloud.mc.dto;

import com.fit2cloud.mc.model.McSysStats;
import io.swagger.annotations.ApiModelProperty;

public class McSysStatsDTO extends McSysStats {

    @ApiModelProperty("状态")
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
