package com.fit2cloud.commons.server.base.domain;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

public class ProxyResource implements Serializable {
    @ApiModelProperty("")
    private String id;

    @ApiModelProperty("")
    private String proxyId;

    @ApiModelProperty("")
    private String resourceType;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table proxy_resource
     *
     * @mbg.generated
     */
    private static final long serialVersionUID = 1L;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column proxy_resource.id
     *
     * @return the value of proxy_resource.id
     *
     * @mbg.generated
     */
    public String getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column proxy_resource.id
     *
     * @param id the value for proxy_resource.id
     *
     * @mbg.generated
     */
    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column proxy_resource.proxy_id
     *
     * @return the value of proxy_resource.proxy_id
     *
     * @mbg.generated
     */
    public String getProxyId() {
        return proxyId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column proxy_resource.proxy_id
     *
     * @param proxyId the value for proxy_resource.proxy_id
     *
     * @mbg.generated
     */
    public void setProxyId(String proxyId) {
        this.proxyId = proxyId == null ? null : proxyId.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column proxy_resource.resource_type
     *
     * @return the value of proxy_resource.resource_type
     *
     * @mbg.generated
     */
    public String getResourceType() {
        return resourceType;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column proxy_resource.resource_type
     *
     * @param resourceType the value for proxy_resource.resource_type
     *
     * @mbg.generated
     */
    public void setResourceType(String resourceType) {
        this.resourceType = resourceType == null ? null : resourceType.trim();
    }
}