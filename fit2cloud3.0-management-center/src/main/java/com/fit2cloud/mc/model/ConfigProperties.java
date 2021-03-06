package com.fit2cloud.mc.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

public class ConfigProperties implements Serializable {
    @ApiModelProperty("配置唯一ID")
    private String id;

    @ApiModelProperty(value = "配置项键", required = true)
    private String confk;

    @ApiModelProperty("配置项值")
    private String confv;

    @ApiModelProperty(value = "配置应用名称", required = true)
    private String application;

    @ApiModelProperty(value = "配置对应profile", required = true)
    private String profile;

    @ApiModelProperty("配置分支")
    private String label;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table config_properties
     *
     * @mbg.generated
     */
    private static final long serialVersionUID = 1L;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column config_properties.id
     *
     * @return the value of config_properties.id
     *
     * @mbg.generated
     */
    public String getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column config_properties.id
     *
     * @param id the value for config_properties.id
     *
     * @mbg.generated
     */
    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column config_properties.confk
     *
     * @return the value of config_properties.confk
     *
     * @mbg.generated
     */
    public String getConfk() {
        return confk;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column config_properties.confk
     *
     * @param confk the value for config_properties.confk
     *
     * @mbg.generated
     */
    public void setConfk(String confk) {
        this.confk = confk == null ? null : confk.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column config_properties.confv
     *
     * @return the value of config_properties.confv
     *
     * @mbg.generated
     */
    public String getConfv() {
        return confv;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column config_properties.confv
     *
     * @param confv the value for config_properties.confv
     *
     * @mbg.generated
     */
    public void setConfv(String confv) {
        this.confv = confv == null ? null : confv.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column config_properties.application
     *
     * @return the value of config_properties.application
     *
     * @mbg.generated
     */
    public String getApplication() {
        return application;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column config_properties.application
     *
     * @param application the value for config_properties.application
     *
     * @mbg.generated
     */
    public void setApplication(String application) {
        this.application = application == null ? null : application.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column config_properties.profile
     *
     * @return the value of config_properties.profile
     *
     * @mbg.generated
     */
    public String getProfile() {
        return profile;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column config_properties.profile
     *
     * @param profile the value for config_properties.profile
     *
     * @mbg.generated
     */
    public void setProfile(String profile) {
        this.profile = profile == null ? null : profile.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column config_properties.label
     *
     * @return the value of config_properties.label
     *
     * @mbg.generated
     */
    public String getLabel() {
        return label;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column config_properties.label
     *
     * @param label the value for config_properties.label
     *
     * @mbg.generated
     */
    public void setLabel(String label) {
        this.label = label == null ? null : label.trim();
    }
}