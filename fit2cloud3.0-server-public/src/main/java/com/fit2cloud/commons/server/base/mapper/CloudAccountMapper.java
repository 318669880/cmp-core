package com.fit2cloud.commons.server.base.mapper;

import com.fit2cloud.commons.server.base.domain.CloudAccount;
import com.fit2cloud.commons.server.base.domain.CloudAccountExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface CloudAccountMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table cloud_account
     *
     * @mbg.generated
     */
    long countByExample(CloudAccountExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table cloud_account
     *
     * @mbg.generated
     */
    int deleteByExample(CloudAccountExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table cloud_account
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(String id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table cloud_account
     *
     * @mbg.generated
     */
    int insert(CloudAccount record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table cloud_account
     *
     * @mbg.generated
     */
    int insertSelective(CloudAccount record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table cloud_account
     *
     * @mbg.generated
     */
    List<CloudAccount> selectByExample(CloudAccountExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table cloud_account
     *
     * @mbg.generated
     */
    CloudAccount selectByPrimaryKey(String id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table cloud_account
     *
     * @mbg.generated
     */
    int updateByExampleSelective(@Param("record") CloudAccount record, @Param("example") CloudAccountExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table cloud_account
     *
     * @mbg.generated
     */
    int updateByExample(@Param("record") CloudAccount record, @Param("example") CloudAccountExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table cloud_account
     *
     * @mbg.generated
     */
    int updateByPrimaryKeySelective(CloudAccount record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table cloud_account
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(CloudAccount record);
}