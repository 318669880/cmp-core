package com.fit2cloud.commons.server.utils;

import com.fit2cloud.commons.server.constants.SessionConstants;
import com.fit2cloud.commons.server.model.SessionUser;
import com.fit2cloud.commons.utils.GlobalConfigurations;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

/**
 * Author: chunxing
 * Date: 2018/5/16  上午11:10
 * Description:
 */
public class SessionUtils {

    public static SessionUser getUser() {
        try {
            if (getRequest() == null) {
                return null;
            }
            Subject subject = SecurityUtils.getSubject();
            Session session = subject.getSession();
            return (SessionUser) session.getAttribute(SessionConstants.ATTR_USER);
        } catch (Exception e) {
            return null;
        }
    }


    public static List<String> getRoleIdList() {
        return Optional.ofNullable(getUser()).orElse(new SessionUser()).getRoleIdList();
    }


    public static String getWorkspaceId() {
        return Optional.ofNullable(getUser()).orElse(new SessionUser()).getWorkspaceId();
    }

    public static String getOrganizationId() {
        return Optional.ofNullable(getUser()).orElse(new SessionUser()).getOrganizationId();
    }

    public static void putUser(SessionUser user) {
        SecurityUtils.getSubject().getSession().setAttribute(SessionConstants.ATTR_USER, user);
    }

    public static String getRemoteAddress() {
        try {
            HttpServletRequest request = getRequest();
            if (request == null) {
                return StringUtils.EMPTY;
            }
            return getRemoteAddress(request);
        } catch (Exception e) {
            return StringUtils.EMPTY;
        }
    }

    public static HttpServletRequest getRequest() {
        try {
            return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        } catch (NullPointerException npe) {
            return null;
        }
    }

    public static String getRemoteAddress(HttpServletRequest request) {
        try {
            String ip = request.getHeader("X-Forwarded-For");
            if (StringUtils.isNotBlank(ip) && !"unKnown".equalsIgnoreCase(ip)) {
                // 多次反向代理后会有多个ip值，第一个ip才是真实ip
                int index = ip.indexOf(",");
                if (index != -1) {
                    return ip.substring(0, index);
                } else {
                    return ip;
                }
            }
            ip = request.getHeader("X-Real-IP");
            if (StringUtils.isNotBlank(ip) && !"unKnown".equalsIgnoreCase(ip)) {
                return ip;
            }
            ip = request.getHeader("Proxy-Client-IP");
            if (StringUtils.isNotBlank(ip) && !"unKnown".equalsIgnoreCase(ip)) {
                return ip;
            }
            ip = request.getHeader("WL-Proxy-Client-IP");
            if (StringUtils.isNotBlank(ip) && !"unKnown".equalsIgnoreCase(ip)) {
                return ip;
            }
            return request.getRemoteAddr();
        } catch (Exception e) {
            return StringUtils.EMPTY;
        }
    }

    public static String getHttpHeader(String headerName) {
        if (StringUtils.isBlank(headerName)) {
            return null;
        }
        try {
            return getRequest().getHeader(headerName);
        } catch (Exception e) {
            return null;
        }
    }

    public static String getCookieId() {
        try {
            Cookie[] cookies = getRequest().getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (StringUtils.equalsIgnoreCase(GlobalConfigurations.getCookieName(), cookie.getName())) {
                        return cookie.getValue();
                    }
                }
            }
        } catch (Exception e) {
            //do nothing
        }
        return StringUtils.EMPTY;
    }
}
