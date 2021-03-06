package com.fit2cloud.mc.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

public class McSysStats implements Serializable {
    @ApiModelProperty("")
    private String id;

    @ApiModelProperty(value = "宿主机Key", required = true)
    private String statKey;

    @ApiModelProperty("更新时间")
    private Long updateTime;

    @ApiModelProperty(value = "监控信息", required = true)
    private String stats;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table mc_sys_stats
     *
     * @mbg.generated
     */
    private static final long serialVersionUID = 1L;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column mc_sys_stats.id
     *
     * @return the value of mc_sys_stats.id
     *
     * @mbg.generated
     */
    public String getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column mc_sys_stats.id
     *
     * @param id the value for mc_sys_stats.id
     *
     * @mbg.generated
     */
    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column mc_sys_stats.stat_key
     *
     * @return the value of mc_sys_stats.stat_key
     *
     * @mbg.generated
     */
    public String getStatKey() {
        return statKey;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column mc_sys_stats.stat_key
     *
     * @param statKey the value for mc_sys_stats.stat_key
     *
     * @mbg.generated
     */
    public void setStatKey(String statKey) {
        this.statKey = statKey == null ? null : statKey.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column mc_sys_stats.update_time
     *
     * @return the value of mc_sys_stats.update_time
     *
     * @mbg.generated
     */
    public Long getUpdateTime() {
        return updateTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column mc_sys_stats.update_time
     *
     * @param updateTime the value for mc_sys_stats.update_time
     *
     * @mbg.generated
     */
    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column mc_sys_stats.stats
     *
     * @return the value of mc_sys_stats.stats
     *
     * @mbg.generated
     */
    public String getStats() {
        return stats;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column mc_sys_stats.stats
     *
     * @param stats the value for mc_sys_stats.stats
     *
     * @mbg.generated
     */
    public void setStats(String stats) {
        this.stats = stats == null ? null : stats.trim();
    }
}