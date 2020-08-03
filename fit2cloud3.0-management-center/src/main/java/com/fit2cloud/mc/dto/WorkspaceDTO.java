package com.fit2cloud.mc.dto;

import com.fit2cloud.commons.server.base.domain.Workspace;
import io.swagger.annotations.ApiModelProperty;

/**
 * Author: chunxing
 * Date: 2018/5/28  下午5:58
 * Description:
 */
public class WorkspaceDTO extends Workspace {

    @ApiModelProperty("授权用户数量")
    private long countAuthorizedUser;
    @ApiModelProperty("组织名称")
    private String organizationName;

    public long getCountAuthorizedUser() {
        return countAuthorizedUser;
    }

    public void setCountAuthorizedUser(long countAuthorizedUser) {
        this.countAuthorizedUser = countAuthorizedUser;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }
}
