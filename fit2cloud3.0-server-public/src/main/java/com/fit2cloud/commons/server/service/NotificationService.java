package com.fit2cloud.commons.server.service;

import com.fit2cloud.commons.server.base.domain.Notification;
import com.fit2cloud.commons.server.base.domain.NotificationExample;
import com.fit2cloud.commons.server.base.mapper.NotificationMapper;
import com.fit2cloud.commons.server.base.mapper.ext.ExtNotificationMapper;
import com.fit2cloud.commons.server.constants.NotificationConstants;
import com.fit2cloud.commons.server.utils.SessionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class NotificationService {

    @Resource
    private NotificationMapper notificationMapper;

    @Resource
    private ExtNotificationMapper extNotificationMapper;

    /**
     * @deprecated replaced by <code>sendAnnouncement(String subject, String content, String receiver)</code>
     */
    @Deprecated
    public void add(Notification notification, List<String> receivers) {
        notification.setStatus(NotificationConstants.Status.UNREAD.name());
        notification.setCreateTime(System.currentTimeMillis());
        if (!CollectionUtils.isEmpty(receivers)) {
            receivers.forEach(receiver -> {
                        notification.setReceiver(receiver);
                        notificationMapper.insert(notification);
                    }
            );
        }
    }

    public void sendAnnouncement(String subject, String content, String receiver) {
        Notification notification = new Notification();
        notification.setTitle(subject);
        notification.setContent(content);
        notification.setType(NotificationConstants.Type.ANNOUNCEMENT.name());
        notification.setStatus(NotificationConstants.Status.UNREAD.name());
        notification.setCreateTime(System.currentTimeMillis());
        notification.setReceiver(receiver);
        notificationMapper.insert(notification);
    }

    public Notification getNotification(int id) {
        return extNotificationMapper.getNotification(id, SessionUtils.getUser().getId());
    }

    public int readAll() {
        Notification record = new Notification();
        record.setStatus(NotificationConstants.Status.READ.name());
        NotificationExample example = new NotificationExample();
        example.createCriteria().andReceiverEqualTo(SessionUtils.getUser().getId());
        return notificationMapper.updateByExampleSelective(record, example);
    }

    public int countNotification(Notification notification) {
        notification.setReceiver(SessionUtils.getUser().getId());
        return extNotificationMapper.countNotification(notification);
    }

    public int read(int id) {
        Notification record = new Notification();
        record.setStatus(NotificationConstants.Status.READ.name());
        NotificationExample example = new NotificationExample();
        example.createCriteria().andIdEqualTo(id).andReceiverEqualTo(SessionUtils.getUser().getId());
        return notificationMapper.updateByExampleSelective(record, example);
    }

    public List<Notification> listNotification(Notification notification) {
        String search = null;
        if (StringUtils.isNotBlank(notification.getTitle())) {
            search = "%" + notification.getTitle() + "%";
        }
        return extNotificationMapper.listNotification(search, SessionUtils.getUser().getId());
    }

    public List<Notification> listReadNotification(Notification notification) {
        String search = null;
        if (StringUtils.isNotBlank(notification.getTitle())) {
            search = "%" + notification.getTitle() + "%";
        }
        return extNotificationMapper.listReadNotification(search, SessionUtils.getUser().getId());
    }

    public List<Notification> listUnreadNotification(Notification notification) {
        String search = null;
        if (StringUtils.isNotBlank(notification.getTitle())) {
            search = "%" + notification.getTitle() + "%";
        }
        return extNotificationMapper.listUnreadNotification(search, SessionUtils.getUser().getId());
    }


}
