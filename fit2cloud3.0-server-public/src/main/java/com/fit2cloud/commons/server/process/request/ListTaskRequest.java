package com.fit2cloud.commons.server.process.request;

import io.swagger.annotations.ApiModelProperty;

public class ListTaskRequest {

    @ApiModelProperty("模糊查询:流程名称或任务名称")
    private String search;

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }
}
