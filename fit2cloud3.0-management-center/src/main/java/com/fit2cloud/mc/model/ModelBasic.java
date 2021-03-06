package com.fit2cloud.mc.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

public class ModelBasic implements Serializable {
    @ApiModelProperty(value = "模块唯一标识", required = true)
    private String modelUuid;

    @ApiModelProperty("模块名称")
    private String name;

    @ApiModelProperty("模块图标")
    private String icon;

    @ApiModelProperty("安装版本")
    private String lastRevision;

    @ApiModelProperty("模块")
    private String module;

    @ApiModelProperty("概诉")
    private String overview;

    @ApiModelProperty("总结")
    private String summary;

    @ApiModelProperty("当前状态")
    private String currentStatus;

    @ApiModelProperty("k8s环境pod数量")
    private Integer podNum;

    @ApiModelProperty("部署信息")
    private String customData;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table model_basic
     *
     * @mbg.generated
     */
    private static final long serialVersionUID = 1L;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column model_basic.model_uuid
     *
     * @return the value of model_basic.model_uuid
     *
     * @mbg.generated
     */
    public String getModelUuid() {
        return modelUuid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column model_basic.model_uuid
     *
     * @param modelUuid the value for model_basic.model_uuid
     *
     * @mbg.generated
     */
    public void setModelUuid(String modelUuid) {
        this.modelUuid = modelUuid == null ? null : modelUuid.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column model_basic.name
     *
     * @return the value of model_basic.name
     *
     * @mbg.generated
     */
    public String getName() {
        return name;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column model_basic.name
     *
     * @param name the value for model_basic.name
     *
     * @mbg.generated
     */
    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column model_basic.icon
     *
     * @return the value of model_basic.icon
     *
     * @mbg.generated
     */
    public String getIcon() {
        return icon;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column model_basic.icon
     *
     * @param icon the value for model_basic.icon
     *
     * @mbg.generated
     */
    public void setIcon(String icon) {
        this.icon = icon == null ? null : icon.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column model_basic.last_revision
     *
     * @return the value of model_basic.last_revision
     *
     * @mbg.generated
     */
    public String getLastRevision() {
        return lastRevision;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column model_basic.last_revision
     *
     * @param lastRevision the value for model_basic.last_revision
     *
     * @mbg.generated
     */
    public void setLastRevision(String lastRevision) {
        this.lastRevision = lastRevision == null ? null : lastRevision.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column model_basic.module
     *
     * @return the value of model_basic.module
     *
     * @mbg.generated
     */
    public String getModule() {
        return module;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column model_basic.module
     *
     * @param module the value for model_basic.module
     *
     * @mbg.generated
     */
    public void setModule(String module) {
        this.module = module == null ? null : module.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column model_basic.overview
     *
     * @return the value of model_basic.overview
     *
     * @mbg.generated
     */
    public String getOverview() {
        return overview;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column model_basic.overview
     *
     * @param overview the value for model_basic.overview
     *
     * @mbg.generated
     */
    public void setOverview(String overview) {
        this.overview = overview == null ? null : overview.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column model_basic.summary
     *
     * @return the value of model_basic.summary
     *
     * @mbg.generated
     */
    public String getSummary() {
        return summary;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column model_basic.summary
     *
     * @param summary the value for model_basic.summary
     *
     * @mbg.generated
     */
    public void setSummary(String summary) {
        this.summary = summary == null ? null : summary.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column model_basic.current_status
     *
     * @return the value of model_basic.current_status
     *
     * @mbg.generated
     */
    public String getCurrentStatus() {
        return currentStatus;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column model_basic.current_status
     *
     * @param currentStatus the value for model_basic.current_status
     *
     * @mbg.generated
     */
    public void setCurrentStatus(String currentStatus) {
        this.currentStatus = currentStatus == null ? null : currentStatus.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column model_basic.pod_num
     *
     * @return the value of model_basic.pod_num
     *
     * @mbg.generated
     */
    public Integer getPodNum() {
        return podNum;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column model_basic.pod_num
     *
     * @param podNum the value for model_basic.pod_num
     *
     * @mbg.generated
     */
    public void setPodNum(Integer podNum) {
        this.podNum = podNum;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column model_basic.custom_data
     *
     * @return the value of model_basic.custom_data
     *
     * @mbg.generated
     */
    public String getCustomData() {
        return customData;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column model_basic.custom_data
     *
     * @param customData the value for model_basic.custom_data
     *
     * @mbg.generated
     */
    public void setCustomData(String customData) {
        this.customData = customData == null ? null : customData.trim();
    }
}