package com.fit2cloud.commons.server.base.domain;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

public class Module implements Serializable {
    @ApiModelProperty("模块ID")
    private String id;

    @ApiModelProperty(value = "模块名称", required = true)
    private String name;

    @ApiModelProperty("模块类型")
    private String type;

    @ApiModelProperty("")
    private String license;

    @ApiModelProperty("")
    private Boolean auth;

    @ApiModelProperty("模块概要")
    private String summary;

    @ApiModelProperty("模块跳转URL")
    private String moduleUrl;

    @ApiModelProperty("模块端口")
    private Long port;

    @ApiModelProperty("模块状态")
    private String status;

    @ApiModelProperty("是否启用")
    private Boolean active;

    @ApiModelProperty("模块icon")
    private String icon;

    @ApiModelProperty("排序")
    private Integer sort;

    @ApiModelProperty("模块打开方式")
    private String open;

    @ApiModelProperty("版本")
    private String version;

    @ApiModelProperty("创建时间")
    private Long createTime;

    @ApiModelProperty("")
    private String ext1;

    @ApiModelProperty("是否授权和license ")
    private String ext2;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table module
     *
     * @mbg.generated
     */
    private static final long serialVersionUID = 1L;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column module.id
     *
     * @return the value of module.id
     *
     * @mbg.generated
     */
    public String getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column module.id
     *
     * @param id the value for module.id
     *
     * @mbg.generated
     */
    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column module.name
     *
     * @return the value of module.name
     *
     * @mbg.generated
     */
    public String getName() {
        return name;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column module.name
     *
     * @param name the value for module.name
     *
     * @mbg.generated
     */
    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column module.type
     *
     * @return the value of module.type
     *
     * @mbg.generated
     */
    public String getType() {
        return type;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column module.type
     *
     * @param type the value for module.type
     *
     * @mbg.generated
     */
    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column module.license
     *
     * @return the value of module.license
     *
     * @mbg.generated
     */
    public String getLicense() {
        return license;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column module.license
     *
     * @param license the value for module.license
     *
     * @mbg.generated
     */
    public void setLicense(String license) {
        this.license = license == null ? null : license.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column module.auth
     *
     * @return the value of module.auth
     *
     * @mbg.generated
     */
    public Boolean getAuth() {
        return auth;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column module.auth
     *
     * @param auth the value for module.auth
     *
     * @mbg.generated
     */
    public void setAuth(Boolean auth) {
        this.auth = auth;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column module.summary
     *
     * @return the value of module.summary
     *
     * @mbg.generated
     */
    public String getSummary() {
        return summary;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column module.summary
     *
     * @param summary the value for module.summary
     *
     * @mbg.generated
     */
    public void setSummary(String summary) {
        this.summary = summary == null ? null : summary.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column module.module_url
     *
     * @return the value of module.module_url
     *
     * @mbg.generated
     */
    public String getModuleUrl() {
        return moduleUrl;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column module.module_url
     *
     * @param moduleUrl the value for module.module_url
     *
     * @mbg.generated
     */
    public void setModuleUrl(String moduleUrl) {
        this.moduleUrl = moduleUrl == null ? null : moduleUrl.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column module.port
     *
     * @return the value of module.port
     *
     * @mbg.generated
     */
    public Long getPort() {
        return port;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column module.port
     *
     * @param port the value for module.port
     *
     * @mbg.generated
     */
    public void setPort(Long port) {
        this.port = port;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column module.status
     *
     * @return the value of module.status
     *
     * @mbg.generated
     */
    public String getStatus() {
        return status;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column module.status
     *
     * @param status the value for module.status
     *
     * @mbg.generated
     */
    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column module.active
     *
     * @return the value of module.active
     *
     * @mbg.generated
     */
    public Boolean getActive() {
        return active;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column module.active
     *
     * @param active the value for module.active
     *
     * @mbg.generated
     */
    public void setActive(Boolean active) {
        this.active = active;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column module.icon
     *
     * @return the value of module.icon
     *
     * @mbg.generated
     */
    public String getIcon() {
        return icon;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column module.icon
     *
     * @param icon the value for module.icon
     *
     * @mbg.generated
     */
    public void setIcon(String icon) {
        this.icon = icon == null ? null : icon.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column module.sort
     *
     * @return the value of module.sort
     *
     * @mbg.generated
     */
    public Integer getSort() {
        return sort;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column module.sort
     *
     * @param sort the value for module.sort
     *
     * @mbg.generated
     */
    public void setSort(Integer sort) {
        this.sort = sort;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column module.open
     *
     * @return the value of module.open
     *
     * @mbg.generated
     */
    public String getOpen() {
        return open;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column module.open
     *
     * @param open the value for module.open
     *
     * @mbg.generated
     */
    public void setOpen(String open) {
        this.open = open == null ? null : open.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column module.version
     *
     * @return the value of module.version
     *
     * @mbg.generated
     */
    public String getVersion() {
        return version;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column module.version
     *
     * @param version the value for module.version
     *
     * @mbg.generated
     */
    public void setVersion(String version) {
        this.version = version == null ? null : version.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column module.create_time
     *
     * @return the value of module.create_time
     *
     * @mbg.generated
     */
    public Long getCreateTime() {
        return createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column module.create_time
     *
     * @param createTime the value for module.create_time
     *
     * @mbg.generated
     */
    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column module.ext1
     *
     * @return the value of module.ext1
     *
     * @mbg.generated
     */
    public String getExt1() {
        return ext1;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column module.ext1
     *
     * @param ext1 the value for module.ext1
     *
     * @mbg.generated
     */
    public void setExt1(String ext1) {
        this.ext1 = ext1 == null ? null : ext1.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column module.ext2
     *
     * @return the value of module.ext2
     *
     * @mbg.generated
     */
    public String getExt2() {
        return ext2;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column module.ext2
     *
     * @param ext2 the value for module.ext2
     *
     * @mbg.generated
     */
    public void setExt2(String ext2) {
        this.ext2 = ext2 == null ? null : ext2.trim();
    }
}