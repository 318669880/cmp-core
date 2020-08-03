package com.fit2cloud.commons.server.base.domain;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

public class TagMapping implements Serializable {
    @ApiModelProperty("")
    private String id;

    @ApiModelProperty(value = "资源ID", required = true)
    private String resourceId;

    @ApiModelProperty(value = "资源类型", required = true)
    private String resourceType;

    @ApiModelProperty(value = "标签标识", required = true)
    private String tagKey;

    @ApiModelProperty("标签值ID")
    private String tagValueId;

    @ApiModelProperty("创建时间")
    private Long createTime;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table tag_mapping
     *
     * @mbg.generated
     */
    private static final long serialVersionUID = 1L;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column tag_mapping.id
     *
     * @return the value of tag_mapping.id
     *
     * @mbg.generated
     */
    public String getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column tag_mapping.id
     *
     * @param id the value for tag_mapping.id
     *
     * @mbg.generated
     */
    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column tag_mapping.resource_id
     *
     * @return the value of tag_mapping.resource_id
     *
     * @mbg.generated
     */
    public String getResourceId() {
        return resourceId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column tag_mapping.resource_id
     *
     * @param resourceId the value for tag_mapping.resource_id
     *
     * @mbg.generated
     */
    public void setResourceId(String resourceId) {
        this.resourceId = resourceId == null ? null : resourceId.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column tag_mapping.resource_type
     *
     * @return the value of tag_mapping.resource_type
     *
     * @mbg.generated
     */
    public String getResourceType() {
        return resourceType;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column tag_mapping.resource_type
     *
     * @param resourceType the value for tag_mapping.resource_type
     *
     * @mbg.generated
     */
    public void setResourceType(String resourceType) {
        this.resourceType = resourceType == null ? null : resourceType.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column tag_mapping.tag_key
     *
     * @return the value of tag_mapping.tag_key
     *
     * @mbg.generated
     */
    public String getTagKey() {
        return tagKey;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column tag_mapping.tag_key
     *
     * @param tagKey the value for tag_mapping.tag_key
     *
     * @mbg.generated
     */
    public void setTagKey(String tagKey) {
        this.tagKey = tagKey == null ? null : tagKey.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column tag_mapping.tag_value_id
     *
     * @return the value of tag_mapping.tag_value_id
     *
     * @mbg.generated
     */
    public String getTagValueId() {
        return tagValueId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column tag_mapping.tag_value_id
     *
     * @param tagValueId the value for tag_mapping.tag_value_id
     *
     * @mbg.generated
     */
    public void setTagValueId(String tagValueId) {
        this.tagValueId = tagValueId == null ? null : tagValueId.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column tag_mapping.create_time
     *
     * @return the value of tag_mapping.create_time
     *
     * @mbg.generated
     */
    public Long getCreateTime() {
        return createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column tag_mapping.create_time
     *
     * @param createTime the value for tag_mapping.create_time
     *
     * @mbg.generated
     */
    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }
}