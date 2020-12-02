package com.fit2cloud.commons.server.base.mapper.ext;

import com.fit2cloud.commons.server.model.UserNotificationSettingDTO;
import org.apache.ibatis.annotations.Param;

public interface ExtUserNotificationMapper {

    UserNotificationSettingDTO getUserNotification(@Param("id") String id);
}
