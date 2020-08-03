package com.fit2cloud.commons.server.security;

import com.fit2cloud.commons.server.utils.SessionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.eis.JavaUuidSessionIdGenerator;
import org.apache.shiro.session.mgt.eis.SessionIdGenerator;

import java.io.Serializable;

/**
 * Created by liqiang on 2018/6/13.
 */
public class CookieIdGenerator extends JavaUuidSessionIdGenerator implements SessionIdGenerator {
    @Override
    public Serializable generateId(Session session) {
        Serializable sessionId = null;
        String cookie = SessionUtils.getCookieId();
        if (StringUtils.isNotBlank(cookie)) {
            sessionId = cookie;
        } else {
            sessionId = super.generateId(session);
        }
        return sessionId;
    }
}
