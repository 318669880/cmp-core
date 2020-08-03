package com.fit2cloud.mc.dto;

import com.fit2cloud.commons.server.base.domain.User;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * Author: chunxing
 * Date: 2018/5/23  下午12:13
 * Description:
 */
@ApiModel(parent = User.class)
public class UserOperateDTO extends User {

    @ApiModelProperty(value = "角色信息列表")
    private List<RoleInfo> roleInfoList;

    public List<RoleInfo> getRoleInfoList() {
        return roleInfoList;
    }

    public void setRoleInfoList(List<RoleInfo> roleInfoList) {
        this.roleInfoList = roleInfoList;
    }
}
