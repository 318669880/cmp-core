package com.fit2cloud.commons.server.model;

import io.swagger.annotations.ApiModelProperty;

public class UserRoleDTO {

    @ApiModelProperty("权限ID")
    private String id;
    @ApiModelProperty(value = "权限类型", allowableValues = "admin,organization,workspace,other")
    private String type;
    @ApiModelProperty("权限名称")
    private String name;
    @ApiModelProperty("描述")
    private String desc;
    @ApiModelProperty("父ID")
    private String parentId;
    @ApiModelProperty("是否可切换")
    private Boolean switchable = true;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getSwitchable() {
        return switchable;
    }

    public void setSwitchable(Boolean switchable) {
        this.switchable = switchable;
    }

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

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }
}
