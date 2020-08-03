package com.fit2cloud.commons.server.utils;

import com.fit2cloud.commons.server.base.domain.User;
import com.fit2cloud.commons.server.security.SsoSessionHandler;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

/**
 * 服务之间调用，需要添加HttpHeader,获取的时候注意当前线程的位置
 */
public class HttpHeaderUtils {

    private static ThreadLocal<User> sessionUserThreadLocal = new ThreadLocal<>();

    public static HttpHeaders getHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_UTF8.toString());
        headers.add(HttpHeaders.COOKIE, SessionUtils.getHttpHeader(HttpHeaders.COOKIE));
//        try {
//            String sourceId = SessionUtils.getUser().getLastSourceId();
//            if (StringUtils.isNotBlank(sourceId)) {
//                headers.add(SsoSessionHandler.SSO_SOURCE_ID, sourceId);
//            }
//        } catch (Exception e) {
//            // do nothing
//        }

        if (StringUtils.isNotBlank(SessionUtils.getHttpHeader(SsoSessionHandler.SSO_HEADER_AUTH_NAME))) {
            headers.add(SsoSessionHandler.SSO_HEADER_AUTH_NAME, SessionUtils.getHttpHeader(SsoSessionHandler.SSO_HEADER_AUTH_NAME));
        } else {
            try {
                headers.add(SsoSessionHandler.SSO_HEADER_AUTH_NAME, SsoSessionHandler.generateId(SessionUtils.getUser().getId()));
            } catch (Exception e) {
                // do nothing
            }
        }
        if (StringUtils.isNotBlank(SessionUtils.getHttpHeader(SsoSessionHandler.SSO_SOURCE_ID))) {
            headers.add(SsoSessionHandler.SSO_SOURCE_ID, SessionUtils.getHttpHeader(SsoSessionHandler.SSO_SOURCE_ID));
        }

        if (sessionUserThreadLocal.get() != null) {
            try {
                if (StringUtils.isNotBlank(sessionUserThreadLocal.get().getId())) {
                    headers.add(SsoSessionHandler.SSO_HEADER_AUTH_NAME, SsoSessionHandler.generateId(sessionUserThreadLocal.get().getId()));
                }
                if (StringUtils.isNotBlank(sessionUserThreadLocal.get().getLastSourceId())) {
                    headers.add(SsoSessionHandler.SSO_SOURCE_ID, sessionUserThreadLocal.get().getLastSourceId());
                }
                headers.remove(HttpHeaders.COOKIE);
            } finally {
                sessionUserThreadLocal.remove();
            }

        }
        return headers;
    }

    public static void runAsUser(User user) {
        if (user != null) {
            if (StringUtils.isBlank(user.getId())) {
                throw new IllegalArgumentException("User ID can't be null or empty.");
            }
            sessionUserThreadLocal.set(user);
        } else {
            sessionUserThreadLocal.remove();
        }
    }
}
