package com.fit2cloud.commons.server.model;

import com.fit2cloud.commons.server.base.domain.CloudServerCredential;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

public class CloudServerManageInfoDTO {
    @ApiModelProperty("操作系统")
    private String os;
    @ApiModelProperty("操作系统版本")
    private String osVersion;
    @ApiModelProperty("ip类型")
    private String ipType;
    @ApiModelProperty("管理IP:ipv4")
    private String managementIp;
    @ApiModelProperty("管理IP:ipv6")
    private String managementIpv6;
    @ApiModelProperty("管理端口")
    private Integer managementPort;
    private List<CloudServerCredential> credentialList;

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getOsVersion() {
        return osVersion;
    }

    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    public Integer getManagementPort() {
        return managementPort;
    }

    public void setManagementPort(Integer managementPort) {
        this.managementPort = managementPort;
    }

    public List<CloudServerCredential> getCredentialList() {
        return credentialList;
    }

    public void setCredentialList(List<CloudServerCredential> credentialList) {
        this.credentialList = credentialList;
    }

    public String getIpType() {
        return ipType;
    }

    public void setIpType(String ipType) {
        this.ipType = ipType;
    }

    public String getManagementIp() {
        return managementIp;
    }

    public void setManagementIpv4(String managementIpv4) {
        this.managementIp = managementIp;
    }

    public String getManagementIpv6() {
        return managementIpv6;
    }

    public void setManagementIpv6(String managementIpv6) {
        this.managementIpv6 = managementIpv6;
    }
}
