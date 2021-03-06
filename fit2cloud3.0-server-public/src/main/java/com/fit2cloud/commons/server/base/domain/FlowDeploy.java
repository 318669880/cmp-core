package com.fit2cloud.commons.server.base.domain;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

public class FlowDeploy implements Serializable {
    @ApiModelProperty(value = "流程模型部署ID", required = true)
    private String deployId;

    @ApiModelProperty(value = "部署版本", required = true)
    private Long deployVersion;

    @ApiModelProperty("部署时间")
    private Long deployTime;

    @ApiModelProperty("流程模型ID")
    private String modelId;

    @ApiModelProperty("部署内容")
    private String deployContent;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table flow_deploy
     *
     * @mbg.generated
     */
    private static final long serialVersionUID = 1L;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column flow_deploy.deploy_id
     *
     * @return the value of flow_deploy.deploy_id
     *
     * @mbg.generated
     */
    public String getDeployId() {
        return deployId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column flow_deploy.deploy_id
     *
     * @param deployId the value for flow_deploy.deploy_id
     *
     * @mbg.generated
     */
    public void setDeployId(String deployId) {
        this.deployId = deployId == null ? null : deployId.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column flow_deploy.deploy_version
     *
     * @return the value of flow_deploy.deploy_version
     *
     * @mbg.generated
     */
    public Long getDeployVersion() {
        return deployVersion;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column flow_deploy.deploy_version
     *
     * @param deployVersion the value for flow_deploy.deploy_version
     *
     * @mbg.generated
     */
    public void setDeployVersion(Long deployVersion) {
        this.deployVersion = deployVersion;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column flow_deploy.deploy_time
     *
     * @return the value of flow_deploy.deploy_time
     *
     * @mbg.generated
     */
    public Long getDeployTime() {
        return deployTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column flow_deploy.deploy_time
     *
     * @param deployTime the value for flow_deploy.deploy_time
     *
     * @mbg.generated
     */
    public void setDeployTime(Long deployTime) {
        this.deployTime = deployTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column flow_deploy.model_id
     *
     * @return the value of flow_deploy.model_id
     *
     * @mbg.generated
     */
    public String getModelId() {
        return modelId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column flow_deploy.model_id
     *
     * @param modelId the value for flow_deploy.model_id
     *
     * @mbg.generated
     */
    public void setModelId(String modelId) {
        this.modelId = modelId == null ? null : modelId.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column flow_deploy.deploy_content
     *
     * @return the value of flow_deploy.deploy_content
     *
     * @mbg.generated
     */
    public String getDeployContent() {
        return deployContent;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column flow_deploy.deploy_content
     *
     * @param deployContent the value for flow_deploy.deploy_content
     *
     * @mbg.generated
     */
    public void setDeployContent(String deployContent) {
        this.deployContent = deployContent == null ? null : deployContent.trim();
    }
}