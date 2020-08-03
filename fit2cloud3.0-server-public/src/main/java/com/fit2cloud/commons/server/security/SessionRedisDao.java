package com.fit2cloud.commons.server.security;

import com.fit2cloud.commons.server.constants.SessionConstants;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.eis.CachingSessionDAO;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.concurrent.TimeUnit;


/**
 * Author: chunxing
 * Date: 2018/5/14  下午4:17
 * Description:
 */
@Component
public class SessionRedisDao extends CachingSessionDAO {

    @Resource
    private RedisTemplate sessionRedisTemplate;

    @Override
    protected Serializable doCreate(Session session) {
        Serializable sessionId = this.generateSessionId(session);
        this.assignSessionId(session, sessionId);
        this.setShiroSession(sessionId.toString(), session);
        return sessionId;
    }

    @Override
    protected Session doReadSession(Serializable sessionId) {
        Session session = getShiroSession(sessionId.toString());
        if (session == null) {
            super.uncache(session);
        }
        return getShiroSession(sessionId.toString());
    }

    @Override
    protected void doUpdate(Session session) {
        this.setShiroSession(session.getId().toString(), session);
    }

    @Override
    protected void doDelete(Session session) {
        super.uncache(session);
        sessionRedisTemplate.delete(sessionCacheKey(session.getId().toString()));
    }

    private Session getShiroSession(String key) {
        return (Session) sessionRedisTemplate.opsForValue().get(sessionCacheKey(key));
    }

    private void setShiroSession(String key, Session session) {
        sessionRedisTemplate.opsForValue().set(sessionCacheKey(key), session, SessionConstants.expired, TimeUnit.MILLISECONDS);
    }

    private String sessionCacheKey(Object key) {
        String sessionCacheName = "fit2cloud:http-sessionss:";
        return sessionCacheName + key;
    }
}
