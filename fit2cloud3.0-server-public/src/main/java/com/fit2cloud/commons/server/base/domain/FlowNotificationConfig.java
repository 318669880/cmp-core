package com.fit2cloud.commons.server.base.domain;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

public class FlowNotificationConfig implements Serializable {
    @ApiModelProperty("消息配置ID")
    private Integer id;

    @ApiModelProperty("流程模型ID")
    private String modelId;

    @ApiModelProperty("环节ID")
    private String activityId;

    @ApiModelProperty("环节顺序号")
    private Integer step;

    @ApiModelProperty("流程类型")
    private String processType;

    @ApiModelProperty("消息类型")
    private String smsType;

    @ApiModelProperty("触发时机")
    private String position;

    @ApiModelProperty("触发操作")
    private String operation;

    @ApiModelProperty("模块ID")
    private String module;

    @ApiModelProperty("消息模板")
    private String template;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table flow_notification_config
     *
     * @mbg.generated
     */
    private static final long serialVersionUID = 1L;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column flow_notification_config.id
     *
     * @return the value of flow_notification_config.id
     *
     * @mbg.generated
     */
    public Integer getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column flow_notification_config.id
     *
     * @param id the value for flow_notification_config.id
     *
     * @mbg.generated
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column flow_notification_config.model_id
     *
     * @return the value of flow_notification_config.model_id
     *
     * @mbg.generated
     */
    public String getModelId() {
        return modelId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column flow_notification_config.model_id
     *
     * @param modelId the value for flow_notification_config.model_id
     *
     * @mbg.generated
     */
    public void setModelId(String modelId) {
        this.modelId = modelId == null ? null : modelId.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column flow_notification_config.activity_id
     *
     * @return the value of flow_notification_config.activity_id
     *
     * @mbg.generated
     */
    public String getActivityId() {
        return activityId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column flow_notification_config.activity_id
     *
     * @param activityId the value for flow_notification_config.activity_id
     *
     * @mbg.generated
     */
    public void setActivityId(String activityId) {
        this.activityId = activityId == null ? null : activityId.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column flow_notification_config.step
     *
     * @return the value of flow_notification_config.step
     *
     * @mbg.generated
     */
    public Integer getStep() {
        return step;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column flow_notification_config.step
     *
     * @param step the value for flow_notification_config.step
     *
     * @mbg.generated
     */
    public void setStep(Integer step) {
        this.step = step;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column flow_notification_config.process_type
     *
     * @return the value of flow_notification_config.process_type
     *
     * @mbg.generated
     */
    public String getProcessType() {
        return processType;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column flow_notification_config.process_type
     *
     * @param processType the value for flow_notification_config.process_type
     *
     * @mbg.generated
     */
    public void setProcessType(String processType) {
        this.processType = processType == null ? null : processType.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column flow_notification_config.sms_type
     *
     * @return the value of flow_notification_config.sms_type
     *
     * @mbg.generated
     */
    public String getSmsType() {
        return smsType;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column flow_notification_config.sms_type
     *
     * @param smsType the value for flow_notification_config.sms_type
     *
     * @mbg.generated
     */
    public void setSmsType(String smsType) {
        this.smsType = smsType == null ? null : smsType.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column flow_notification_config.position
     *
     * @return the value of flow_notification_config.position
     *
     * @mbg.generated
     */
    public String getPosition() {
        return position;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column flow_notification_config.position
     *
     * @param position the value for flow_notification_config.position
     *
     * @mbg.generated
     */
    public void setPosition(String position) {
        this.position = position == null ? null : position.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column flow_notification_config.operation
     *
     * @return the value of flow_notification_config.operation
     *
     * @mbg.generated
     */
    public String getOperation() {
        return operation;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column flow_notification_config.operation
     *
     * @param operation the value for flow_notification_config.operation
     *
     * @mbg.generated
     */
    public void setOperation(String operation) {
        this.operation = operation == null ? null : operation.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column flow_notification_config.module
     *
     * @return the value of flow_notification_config.module
     *
     * @mbg.generated
     */
    public String getModule() {
        return module;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column flow_notification_config.module
     *
     * @param module the value for flow_notification_config.module
     *
     * @mbg.generated
     */
    public void setModule(String module) {
        this.module = module == null ? null : module.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column flow_notification_config.template
     *
     * @return the value of flow_notification_config.template
     *
     * @mbg.generated
     */
    public String getTemplate() {
        return template;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column flow_notification_config.template
     *
     * @param template the value for flow_notification_config.template
     *
     * @mbg.generated
     */
    public void setTemplate(String template) {
        this.template = template == null ? null : template.trim();
    }
}