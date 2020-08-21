package com.fit2cloud.commons.server.base.mapper;

import com.fit2cloud.commons.server.base.domain.TLock;
import com.fit2cloud.commons.server.base.domain.TLockExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TLockMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_lock
     *
     * @mbg.generated
     */
    long countByExample(TLockExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_lock
     *
     * @mbg.generated
     */
    int deleteByExample(TLockExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_lock
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(String lockKey);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_lock
     *
     * @mbg.generated
     */
    int insert(TLock record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_lock
     *
     * @mbg.generated
     */
    int insertSelective(TLock record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_lock
     *
     * @mbg.generated
     */
    List<TLock> selectByExample(TLockExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_lock
     *
     * @mbg.generated
     */
    TLock selectByPrimaryKey(String lockKey);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_lock
     *
     * @mbg.generated
     */
    int updateByExampleSelective(@Param("record") TLock record, @Param("example") TLockExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_lock
     *
     * @mbg.generated
     */
    int updateByExample(@Param("record") TLock record, @Param("example") TLockExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_lock
     *
     * @mbg.generated
     */
    int updateByPrimaryKeySelective(TLock record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_lock
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(TLock record);
}