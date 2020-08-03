package com.fit2cloud.mc.dto;

import com.fit2cloud.commons.server.base.domain.Organization;
import io.swagger.annotations.ApiModelProperty;

/**
 * Author: chunxing
 * Date: 2018/5/30  下午6:43
 * Description:
 */
public class OrganizationDTO extends Organization {

    @ApiModelProperty("工作空间数量")
    private long countWorkspace;
    @ApiModelProperty("组织数量")
    private long countOrgAdmin;

    public long getCountWorkspace() {
        return countWorkspace;
    }

    public void setCountWorkspace(long countWorkspace) {
        this.countWorkspace = countWorkspace;
    }

    public long getCountOrgAdmin() {
        return countOrgAdmin;
    }

    public void setCountOrgAdmin(long countOrgAdmin) {
        this.countOrgAdmin = countOrgAdmin;
    }
}
