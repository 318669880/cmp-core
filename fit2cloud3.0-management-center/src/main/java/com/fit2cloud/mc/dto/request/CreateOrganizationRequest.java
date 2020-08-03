package com.fit2cloud.mc.dto.request;

import io.swagger.annotations.ApiModelProperty;

public class CreateOrganizationRequest {


    @ApiModelProperty(value = "组织名称", required = true)
    private String name;

    @ApiModelProperty("描述")
    private String description;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
