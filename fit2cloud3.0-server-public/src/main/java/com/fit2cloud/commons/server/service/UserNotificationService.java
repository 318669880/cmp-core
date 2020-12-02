package com.fit2cloud.commons.server.service;

import com.fit2cloud.commons.server.base.domain.User;
import com.fit2cloud.commons.server.base.domain.UserNotificationSetting;
import com.fit2cloud.commons.server.base.domain.UserNotificationSettingExample;
import com.fit2cloud.commons.server.base.mapper.UserMapper;
import com.fit2cloud.commons.server.base.mapper.UserNotificationSettingMapper;
import com.fit2cloud.commons.server.base.mapper.ext.ExtUserNotificationMapper;
import com.fit2cloud.commons.server.model.UserNotificationSettingDTO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Author gin
 * @Date 2020/12/2 11:03 上午
 */
@Service
public class UserNotificationService {
    @Resource
    private UserNotificationSettingMapper userNotificationSettingMapper;
    @Resource
    private ExtUserNotificationMapper extUserNotificationMapper;
    @Resource
    private UserMapper userMapper;

    public UserNotificationSettingDTO getUserNotification(String id) {
        return extUserNotificationMapper.getUserNotification(id);
    }

    public void updateUserNotification(UserNotificationSettingDTO userNotificationSetting) {
        User user = userMapper.selectByPrimaryKey(userNotificationSetting.getId());
        user.setEmail(userNotificationSetting.getEmail());
        user.setPhone(userNotificationSetting.getPhone());
        userMapper.updateByPrimaryKey(user);

        UserNotificationSetting notificationSetting = new UserNotificationSetting();
        notificationSetting.setUserId(userNotificationSetting.getId());
        notificationSetting.setWechatAccount(userNotificationSetting.getWechatAccount());
        int i = userNotificationSettingMapper.updateByPrimaryKey(notificationSetting);
        if (i != 1) {
            userNotificationSettingMapper.insert(notificationSetting);
        }
    }
}
