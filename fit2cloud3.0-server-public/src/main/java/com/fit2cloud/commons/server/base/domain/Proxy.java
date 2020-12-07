package com.fit2cloud.commons.server.base.domain;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

public class Proxy implements Serializable {
    @ApiModelProperty("")
    private String id;

    @ApiModelProperty("")
    private Integer port;

    @ApiModelProperty("")
    private String username;

    @ApiModelProperty("")
    private String password;

    @ApiModelProperty("")
    private String scope;

    @ApiModelProperty("")
    private String organizationId;

    @ApiModelProperty("")
    private String ip;

    @ApiModelProperty("")
    private Long createdTime;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table devops_proxy
     *
     * @mbg.generated
     */
    private static final long serialVersionUID = 1L;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column devops_proxy.id
     *
     * @return the value of devops_proxy.id
     *
     * @mbg.generated
     */
    public String getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column devops_proxy.id
     *
     * @param id the value for devops_proxy.id
     *
     * @mbg.generated
     */
    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column devops_proxy.port
     *
     * @return the value of devops_proxy.port
     *
     * @mbg.generated
     */
    public Integer getPort() {
        return port;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column devops_proxy.port
     *
     * @param port the value for devops_proxy.port
     *
     * @mbg.generated
     */
    public void setPort(Integer port) {
        this.port = port;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column devops_proxy.username
     *
     * @return the value of devops_proxy.username
     *
     * @mbg.generated
     */
    public String getUsername() {
        return username;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column devops_proxy.username
     *
     * @param username the value for devops_proxy.username
     *
     * @mbg.generated
     */
    public void setUsername(String username) {
        this.username = username == null ? null : username.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column devops_proxy.password
     *
     * @return the value of devops_proxy.password
     *
     * @mbg.generated
     */
    public String getPassword() {
        return password;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column devops_proxy.password
     *
     * @param password the value for devops_proxy.password
     *
     * @mbg.generated
     */
    public void setPassword(String password) {
        this.password = password == null ? null : password.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column devops_proxy.scope
     *
     * @return the value of devops_proxy.scope
     *
     * @mbg.generated
     */
    public String getScope() {
        return scope;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column devops_proxy.scope
     *
     * @param scope the value for devops_proxy.scope
     *
     * @mbg.generated
     */
    public void setScope(String scope) {
        this.scope = scope == null ? null : scope.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column devops_proxy.organization_id
     *
     * @return the value of devops_proxy.organization_id
     *
     * @mbg.generated
     */
    public String getOrganizationId() {
        return organizationId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column devops_proxy.organization_id
     *
     * @param organizationId the value for devops_proxy.organization_id
     *
     * @mbg.generated
     */
    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId == null ? null : organizationId.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column devops_proxy.ip
     *
     * @return the value of devops_proxy.ip
     *
     * @mbg.generated
     */
    public String getIp() {
        return ip;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column devops_proxy.ip
     *
     * @param ip the value for devops_proxy.ip
     *
     * @mbg.generated
     */
    public void setIp(String ip) {
        this.ip = ip == null ? null : ip.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column devops_proxy.created_time
     *
     * @return the value of devops_proxy.created_time
     *
     * @mbg.generated
     */
    public Long getCreatedTime() {
        return createdTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column devops_proxy.created_time
     *
     * @param createdTime the value for devops_proxy.created_time
     *
     * @mbg.generated
     */
    public void setCreatedTime(Long createdTime) {
        this.createdTime = createdTime;
    }
}