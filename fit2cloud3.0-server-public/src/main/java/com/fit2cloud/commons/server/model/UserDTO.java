package com.fit2cloud.commons.server.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fit2cloud.commons.server.base.domain.Role;
import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liqiang on 2018/11/23.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDTO {

    @ApiModelProperty("用户ID")
    private String id;

    @ApiModelProperty(value = "用户名", required = true)
    private String name;

    @ApiModelProperty("邮箱")
    private String email;

    @ApiModelProperty(value = "用户类型", required = true)
    private String source;

    @ApiModelProperty(value = "用户状态", required = true)
    private Boolean active;

    @ApiModelProperty("电话")
    private String phone;

    @ApiModelProperty("创建时间")
    private Long createTime;

    @ApiModelProperty("上一次访问资源ID")
    private String lastSourceId;

    @ApiModelProperty("角色列表")
    private List<Role> roles = new ArrayList<>();

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public String getLastSourceId() {
        return lastSourceId;
    }

    public void setLastSourceId(String lastSourceId) {
        this.lastSourceId = lastSourceId;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }
}
