package com.fit2cloud.commons.server.base.domain;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

public class OperationLog implements Serializable {
    @ApiModelProperty("")
    private String id;

    @ApiModelProperty(value = "工作空间ID", required = true)
    private String workspaceId;

    @ApiModelProperty(value = "工作空间名称", required = true)
    private String workspaceName;

    @ApiModelProperty(value = "资源拥有者ID", required = true)
    private String resourceUserId;

    @ApiModelProperty(value = "资源拥有者名称", required = true)
    private String resourceUserName;

    @ApiModelProperty(value = "资源类型", required = true)
    private String resourceType;

    @ApiModelProperty("资源ID")
    private String resourceId;

    @ApiModelProperty("资源名称")
    private String resourceName;

    @ApiModelProperty(value = "操作", required = true)
    private String operation;

    @ApiModelProperty(value = "操作时间", required = true)
    private Long time;

    @ApiModelProperty("模块")
    private String module;

    @ApiModelProperty("操作方IP")
    private String sourceIp;

    @ApiModelProperty("操作信息")
    private String message;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table operation_log
     *
     * @mbg.generated
     */
    private static final long serialVersionUID = 1L;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column operation_log.id
     *
     * @return the value of operation_log.id
     *
     * @mbg.generated
     */
    public String getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column operation_log.id
     *
     * @param id the value for operation_log.id
     *
     * @mbg.generated
     */
    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column operation_log.workspace_id
     *
     * @return the value of operation_log.workspace_id
     *
     * @mbg.generated
     */
    public String getWorkspaceId() {
        return workspaceId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column operation_log.workspace_id
     *
     * @param workspaceId the value for operation_log.workspace_id
     *
     * @mbg.generated
     */
    public void setWorkspaceId(String workspaceId) {
        this.workspaceId = workspaceId == null ? null : workspaceId.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column operation_log.workspace_name
     *
     * @return the value of operation_log.workspace_name
     *
     * @mbg.generated
     */
    public String getWorkspaceName() {
        return workspaceName;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column operation_log.workspace_name
     *
     * @param workspaceName the value for operation_log.workspace_name
     *
     * @mbg.generated
     */
    public void setWorkspaceName(String workspaceName) {
        this.workspaceName = workspaceName == null ? null : workspaceName.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column operation_log.resource_user_id
     *
     * @return the value of operation_log.resource_user_id
     *
     * @mbg.generated
     */
    public String getResourceUserId() {
        return resourceUserId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column operation_log.resource_user_id
     *
     * @param resourceUserId the value for operation_log.resource_user_id
     *
     * @mbg.generated
     */
    public void setResourceUserId(String resourceUserId) {
        this.resourceUserId = resourceUserId == null ? null : resourceUserId.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column operation_log.resource_user_name
     *
     * @return the value of operation_log.resource_user_name
     *
     * @mbg.generated
     */
    public String getResourceUserName() {
        return resourceUserName;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column operation_log.resource_user_name
     *
     * @param resourceUserName the value for operation_log.resource_user_name
     *
     * @mbg.generated
     */
    public void setResourceUserName(String resourceUserName) {
        this.resourceUserName = resourceUserName == null ? null : resourceUserName.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column operation_log.resource_type
     *
     * @return the value of operation_log.resource_type
     *
     * @mbg.generated
     */
    public String getResourceType() {
        return resourceType;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column operation_log.resource_type
     *
     * @param resourceType the value for operation_log.resource_type
     *
     * @mbg.generated
     */
    public void setResourceType(String resourceType) {
        this.resourceType = resourceType == null ? null : resourceType.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column operation_log.resource_id
     *
     * @return the value of operation_log.resource_id
     *
     * @mbg.generated
     */
    public String getResourceId() {
        return resourceId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column operation_log.resource_id
     *
     * @param resourceId the value for operation_log.resource_id
     *
     * @mbg.generated
     */
    public void setResourceId(String resourceId) {
        this.resourceId = resourceId == null ? null : resourceId.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column operation_log.resource_name
     *
     * @return the value of operation_log.resource_name
     *
     * @mbg.generated
     */
    public String getResourceName() {
        return resourceName;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column operation_log.resource_name
     *
     * @param resourceName the value for operation_log.resource_name
     *
     * @mbg.generated
     */
    public void setResourceName(String resourceName) {
        this.resourceName = resourceName == null ? null : resourceName.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column operation_log.operation
     *
     * @return the value of operation_log.operation
     *
     * @mbg.generated
     */
    public String getOperation() {
        return operation;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column operation_log.operation
     *
     * @param operation the value for operation_log.operation
     *
     * @mbg.generated
     */
    public void setOperation(String operation) {
        this.operation = operation == null ? null : operation.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column operation_log.time
     *
     * @return the value of operation_log.time
     *
     * @mbg.generated
     */
    public Long getTime() {
        return time;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column operation_log.time
     *
     * @param time the value for operation_log.time
     *
     * @mbg.generated
     */
    public void setTime(Long time) {
        this.time = time;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column operation_log.module
     *
     * @return the value of operation_log.module
     *
     * @mbg.generated
     */
    public String getModule() {
        return module;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column operation_log.module
     *
     * @param module the value for operation_log.module
     *
     * @mbg.generated
     */
    public void setModule(String module) {
        this.module = module == null ? null : module.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column operation_log.source_ip
     *
     * @return the value of operation_log.source_ip
     *
     * @mbg.generated
     */
    public String getSourceIp() {
        return sourceIp;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column operation_log.source_ip
     *
     * @param sourceIp the value for operation_log.source_ip
     *
     * @mbg.generated
     */
    public void setSourceIp(String sourceIp) {
        this.sourceIp = sourceIp == null ? null : sourceIp.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column operation_log.message
     *
     * @return the value of operation_log.message
     *
     * @mbg.generated
     */
    public String getMessage() {
        return message;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column operation_log.message
     *
     * @param message the value for operation_log.message
     *
     * @mbg.generated
     */
    public void setMessage(String message) {
        this.message = message == null ? null : message.trim();
    }
}