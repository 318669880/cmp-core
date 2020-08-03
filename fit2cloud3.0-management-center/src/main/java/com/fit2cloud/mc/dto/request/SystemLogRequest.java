package com.fit2cloud.mc.dto.request;

import io.swagger.annotations.ApiModelProperty;

public class SystemLogRequest {

    @ApiModelProperty("模块ID")
    private String module;

    @ApiModelProperty("级别")
    private String level;

    @ApiModelProperty("详情")
    private String message;

    @ApiModelProperty("开始时间")
    private long logTimeStart;

    @ApiModelProperty("结束时间")
    private long logTimeEnd;

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getLogTimeStart() {
        return logTimeStart;
    }

    public void setLogTimeStart(long logTimeStart) {
        this.logTimeStart = logTimeStart;
    }

    public long getLogTimeEnd() {
        return logTimeEnd;
    }

    public void setLogTimeEnd(long logTimeEnd) {
        this.logTimeEnd = logTimeEnd;
    }
}
