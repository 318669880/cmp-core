package com.fit2cloud.mc.dao;

import com.fit2cloud.mc.model.McSysStats;
import com.fit2cloud.mc.model.McSysStatsExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface McSysStatsMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table mc_sys_stats
     *
     * @mbg.generated
     */
    long countByExample(McSysStatsExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table mc_sys_stats
     *
     * @mbg.generated
     */
    int deleteByExample(McSysStatsExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table mc_sys_stats
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(String id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table mc_sys_stats
     *
     * @mbg.generated
     */
    int insert(McSysStats record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table mc_sys_stats
     *
     * @mbg.generated
     */
    int insertSelective(McSysStats record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table mc_sys_stats
     *
     * @mbg.generated
     */
    List<McSysStats> selectByExampleWithBLOBs(McSysStatsExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table mc_sys_stats
     *
     * @mbg.generated
     */
    List<McSysStats> selectByExample(McSysStatsExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table mc_sys_stats
     *
     * @mbg.generated
     */
    McSysStats selectByPrimaryKey(String id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table mc_sys_stats
     *
     * @mbg.generated
     */
    int updateByExampleSelective(@Param("record") McSysStats record, @Param("example") McSysStatsExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table mc_sys_stats
     *
     * @mbg.generated
     */
    int updateByExampleWithBLOBs(@Param("record") McSysStats record, @Param("example") McSysStatsExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table mc_sys_stats
     *
     * @mbg.generated
     */
    int updateByExample(@Param("record") McSysStats record, @Param("example") McSysStatsExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table mc_sys_stats
     *
     * @mbg.generated
     */
    int updateByPrimaryKeySelective(McSysStats record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table mc_sys_stats
     *
     * @mbg.generated
     */
    int updateByPrimaryKeyWithBLOBs(McSysStats record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table mc_sys_stats
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(McSysStats record);
}