package com.fit2cloud.commons.server.base.mapper;

import com.fit2cloud.commons.server.base.domain.UserNotificationSetting;
import com.fit2cloud.commons.server.base.domain.UserNotificationSettingExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface UserNotificationSettingMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user_notification_setting
     *
     * @mbg.generated
     */
    long countByExample(UserNotificationSettingExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user_notification_setting
     *
     * @mbg.generated
     */
    int deleteByExample(UserNotificationSettingExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user_notification_setting
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(String userId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user_notification_setting
     *
     * @mbg.generated
     */
    int insert(UserNotificationSetting record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user_notification_setting
     *
     * @mbg.generated
     */
    int insertSelective(UserNotificationSetting record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user_notification_setting
     *
     * @mbg.generated
     */
    List<UserNotificationSetting> selectByExample(UserNotificationSettingExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user_notification_setting
     *
     * @mbg.generated
     */
    UserNotificationSetting selectByPrimaryKey(String userId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user_notification_setting
     *
     * @mbg.generated
     */
    int updateByExampleSelective(@Param("record") UserNotificationSetting record, @Param("example") UserNotificationSettingExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user_notification_setting
     *
     * @mbg.generated
     */
    int updateByExample(@Param("record") UserNotificationSetting record, @Param("example") UserNotificationSettingExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user_notification_setting
     *
     * @mbg.generated
     */
    int updateByPrimaryKeySelective(UserNotificationSetting record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user_notification_setting
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(UserNotificationSetting record);
}