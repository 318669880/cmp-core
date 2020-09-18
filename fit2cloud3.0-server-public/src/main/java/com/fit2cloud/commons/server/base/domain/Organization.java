package com.fit2cloud.commons.server.base.domain;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

public class Organization implements Serializable {
    @ApiModelProperty("组织 id")
    private String id;

    @ApiModelProperty(value = "组织名称", required = true)
    private String name;

    @ApiModelProperty("描述")
    private String description;

    @ApiModelProperty("创建时间时间戳")
    private Long createTime;

    @ApiModelProperty("上级组织id")
    private String pid;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table organization
     *
     * @mbg.generated
     */
    private static final long serialVersionUID = 1L;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column organization.id
     *
     * @return the value of organization.id
     *
     * @mbg.generated
     */
    public String getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column organization.id
     *
     * @param id the value for organization.id
     *
     * @mbg.generated
     */
    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column organization.name
     *
     * @return the value of organization.name
     *
     * @mbg.generated
     */
    public String getName() {
        return name;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column organization.name
     *
     * @param name the value for organization.name
     *
     * @mbg.generated
     */
    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column organization.description
     *
     * @return the value of organization.description
     *
     * @mbg.generated
     */
    public String getDescription() {
        return description;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column organization.description
     *
     * @param description the value for organization.description
     *
     * @mbg.generated
     */
    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column organization.create_time
     *
     * @return the value of organization.create_time
     *
     * @mbg.generated
     */
    public Long getCreateTime() {
        return createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column organization.create_time
     *
     * @param createTime the value for organization.create_time
     *
     * @mbg.generated
     */
    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column organization.pid
     *
     * @return the value of organization.pid
     *
     * @mbg.generated
     */
    public String getPid() {
        return pid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column organization.pid
     *
     * @param pid the value for organization.pid
     *
     * @mbg.generated
     */
    public void setPid(String pid) {
        this.pid = pid == null ? null : pid.trim();
    }
}