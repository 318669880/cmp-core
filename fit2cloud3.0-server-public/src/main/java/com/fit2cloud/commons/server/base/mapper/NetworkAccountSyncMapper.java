package com.fit2cloud.commons.server.base.mapper;

import com.fit2cloud.commons.server.base.domain.NetworkAccountSync;
import com.fit2cloud.commons.server.base.domain.NetworkAccountSyncExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface NetworkAccountSyncMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table network_account_sync
     *
     * @mbg.generated
     */
    long countByExample(NetworkAccountSyncExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table network_account_sync
     *
     * @mbg.generated
     */
    int deleteByExample(NetworkAccountSyncExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table network_account_sync
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(String accountId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table network_account_sync
     *
     * @mbg.generated
     */
    int insert(NetworkAccountSync record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table network_account_sync
     *
     * @mbg.generated
     */
    int insertSelective(NetworkAccountSync record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table network_account_sync
     *
     * @mbg.generated
     */
    List<NetworkAccountSync> selectByExample(NetworkAccountSyncExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table network_account_sync
     *
     * @mbg.generated
     */
    NetworkAccountSync selectByPrimaryKey(String accountId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table network_account_sync
     *
     * @mbg.generated
     */
    int updateByExampleSelective(@Param("record") NetworkAccountSync record, @Param("example") NetworkAccountSyncExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table network_account_sync
     *
     * @mbg.generated
     */
    int updateByExample(@Param("record") NetworkAccountSync record, @Param("example") NetworkAccountSyncExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table network_account_sync
     *
     * @mbg.generated
     */
    int updateByPrimaryKeySelective(NetworkAccountSync record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table network_account_sync
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(NetworkAccountSync record);
}