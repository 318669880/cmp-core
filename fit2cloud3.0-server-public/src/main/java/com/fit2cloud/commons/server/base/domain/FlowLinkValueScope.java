package com.fit2cloud.commons.server.base.domain;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

public class FlowLinkValueScope implements Serializable {
    @ApiModelProperty("")
    private String id;

    @ApiModelProperty(value = "环节项ID", required = true)
    private String linkValueId;

    @ApiModelProperty(value = "工作空间ID", required = true)
    private String workspaceId;

    @ApiModelProperty("模块ID")
    private String module;

    @ApiModelProperty("类型")
    private String type;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table flow_link_value_scope
     *
     * @mbg.generated
     */
    private static final long serialVersionUID = 1L;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column flow_link_value_scope.id
     *
     * @return the value of flow_link_value_scope.id
     *
     * @mbg.generated
     */
    public String getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column flow_link_value_scope.id
     *
     * @param id the value for flow_link_value_scope.id
     *
     * @mbg.generated
     */
    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column flow_link_value_scope.link_value_id
     *
     * @return the value of flow_link_value_scope.link_value_id
     *
     * @mbg.generated
     */
    public String getLinkValueId() {
        return linkValueId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column flow_link_value_scope.link_value_id
     *
     * @param linkValueId the value for flow_link_value_scope.link_value_id
     *
     * @mbg.generated
     */
    public void setLinkValueId(String linkValueId) {
        this.linkValueId = linkValueId == null ? null : linkValueId.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column flow_link_value_scope.workspace_id
     *
     * @return the value of flow_link_value_scope.workspace_id
     *
     * @mbg.generated
     */
    public String getWorkspaceId() {
        return workspaceId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column flow_link_value_scope.workspace_id
     *
     * @param workspaceId the value for flow_link_value_scope.workspace_id
     *
     * @mbg.generated
     */
    public void setWorkspaceId(String workspaceId) {
        this.workspaceId = workspaceId == null ? null : workspaceId.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column flow_link_value_scope.module
     *
     * @return the value of flow_link_value_scope.module
     *
     * @mbg.generated
     */
    public String getModule() {
        return module;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column flow_link_value_scope.module
     *
     * @param module the value for flow_link_value_scope.module
     *
     * @mbg.generated
     */
    public void setModule(String module) {
        this.module = module == null ? null : module.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column flow_link_value_scope.type
     *
     * @return the value of flow_link_value_scope.type
     *
     * @mbg.generated
     */
    public String getType() {
        return type;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column flow_link_value_scope.type
     *
     * @param type the value for flow_link_value_scope.type
     *
     * @mbg.generated
     */
    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }
}