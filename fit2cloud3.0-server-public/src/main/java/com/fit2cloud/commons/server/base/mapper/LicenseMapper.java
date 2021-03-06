package com.fit2cloud.commons.server.base.mapper;

import com.fit2cloud.commons.server.base.domain.License;
import com.fit2cloud.commons.server.base.domain.LicenseExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface LicenseMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table license
     *
     * @mbg.generated
     */
    long countByExample(LicenseExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table license
     *
     * @mbg.generated
     */
    int deleteByExample(LicenseExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table license
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(String id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table license
     *
     * @mbg.generated
     */
    int insert(License record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table license
     *
     * @mbg.generated
     */
    int insertSelective(License record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table license
     *
     * @mbg.generated
     */
    List<License> selectByExample(LicenseExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table license
     *
     * @mbg.generated
     */
    License selectByPrimaryKey(String id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table license
     *
     * @mbg.generated
     */
    int updateByExampleSelective(@Param("record") License record, @Param("example") LicenseExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table license
     *
     * @mbg.generated
     */
    int updateByExample(@Param("record") License record, @Param("example") LicenseExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table license
     *
     * @mbg.generated
     */
    int updateByPrimaryKeySelective(License record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table license
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(License record);
}