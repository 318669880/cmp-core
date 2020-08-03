package com.fit2cloud.commons.server.base.mapper.ext;

import com.fit2cloud.commons.server.base.domain.Notification;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtNotificationMapper {

    Notification getNotification(@Param("id") Integer id, @Param("receiver") String receiver);

    List<Notification> listNotification(@Param("search") String search, @Param("receiver") String receiver);

    List<Notification> listReadNotification(@Param("search") String search, @Param("receiver") String receiver);

    List<Notification> listUnreadNotification(@Param("search") String search, @Param("receiver") String receiver);

    int countNotification(@Param("notification") Notification notification);

}
