package com.fit2cloud.commons.server.base.mapper;

import com.fit2cloud.commons.server.base.domain.CloudDisk;
import com.fit2cloud.commons.server.base.domain.CloudDiskExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface CloudDiskMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table cloud_disk
     *
     * @mbg.generated
     */
    long countByExample(CloudDiskExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table cloud_disk
     *
     * @mbg.generated
     */
    int deleteByExample(CloudDiskExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table cloud_disk
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(String id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table cloud_disk
     *
     * @mbg.generated
     */
    int insert(CloudDisk record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table cloud_disk
     *
     * @mbg.generated
     */
    int insertSelective(CloudDisk record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table cloud_disk
     *
     * @mbg.generated
     */
    List<CloudDisk> selectByExample(CloudDiskExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table cloud_disk
     *
     * @mbg.generated
     */
    CloudDisk selectByPrimaryKey(String id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table cloud_disk
     *
     * @mbg.generated
     */
    int updateByExampleSelective(@Param("record") CloudDisk record, @Param("example") CloudDiskExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table cloud_disk
     *
     * @mbg.generated
     */
    int updateByExample(@Param("record") CloudDisk record, @Param("example") CloudDiskExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table cloud_disk
     *
     * @mbg.generated
     */
    int updateByPrimaryKeySelective(CloudDisk record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table cloud_disk
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(CloudDisk record);
}