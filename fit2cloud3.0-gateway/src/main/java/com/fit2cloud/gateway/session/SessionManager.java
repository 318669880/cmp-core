package com.fit2cloud.gateway.session;

import com.fit2cloud.commons.server.base.domain.OperationLog;
import com.fit2cloud.commons.server.base.domain.User;
import com.fit2cloud.commons.server.constants.ResourceOperation;
import com.fit2cloud.commons.server.constants.ResourceTypeConstants;
import com.fit2cloud.commons.server.constants.SessionConstants;
import com.fit2cloud.commons.server.service.OperationLogService;
import com.fit2cloud.commons.server.service.UserCommonService;
import com.fit2cloud.commons.server.utils.SessionUtils;
import com.fit2cloud.commons.utils.CommonBeanFactory;
import com.fit2cloud.commons.utils.LogUtil;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by liqiang on 2018/8/27.
 */
public class SessionManager {

    public static void setSessionUser(HttpServletRequest request) {
        if (request == null) {
            return;
        }
        if (request.getUserPrincipal() != null && StringUtils.isNotBlank(request.getUserPrincipal().getName())) {
            if (request.getSession().getAttribute(SessionConstants.ATTR_USER) == null) {
                request.getSession().setAttribute(SessionConstants.ATTR_USER, request.getUserPrincipal().getName());
            }
        }
    }

    public static String getSessionUser(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        setSessionUser(request);
        Object user = request.getSession().getAttribute(SessionConstants.ATTR_USER);
        if (user != null) {
            return (String) user;
        }
        return null;
    }

    public static void delSessionUser(HttpServletRequest request) {
        if (request == null) {
            return;
        }
        request.getSession().removeAttribute(SessionConstants.ATTR_USER);
    }

    public static void logUserLogin(HttpServletRequest request, HttpServletResponse response) {
        try {
            String userName = request.getParameter("username");
            if (StringUtils.isNotBlank(userName)) {
                for (String setCookieValue : response.getHeaders("Set-Cookie")) {
                    if (StringUtils.startsWithIgnoreCase(setCookieValue, "KEYCLOAK_IDENTITY")) {
                        try {
                            User user = CommonBeanFactory.getBean(UserCommonService.class).getUserBySth(userName);
                            if (user != null) {
                                OperationLog log = OperationLogService.createOperationLog(null, user, user.getId(), user.getName(), ResourceTypeConstants.USER.name(), ResourceOperation.LOGIN, null, SessionUtils.getRemoteAddress(request));
                                OperationLogService.log(log);
                            }
                        } catch (Exception e) {
                            LogUtil.error("Record login log exception", e);
                        }
                        break;
                    }
                }
            }
        } catch (Exception e) {
            LogUtil.error("Record login log exception", e);
        }
    }

    public static void logUserLogout(HttpServletRequest request) {
        try {
            String userName = getSessionUser(request);
            delSessionUser(request);
            if (StringUtils.isNotBlank(userName)) {
                try {
                    User user = CommonBeanFactory.getBean(UserCommonService.class).getUserBySth(userName);
                    if (user != null) {
                        OperationLogService.log(null, user, user.getId(), user.getName(), ResourceTypeConstants.USER.name(), ResourceOperation.LOGOUT, null);
                    }
                } catch (Exception e) {
                    LogUtil.error("Record login log exception", e);
                }
            }
        } catch (Exception e) {
            LogUtil.error("Record login log exception", e);
        }
    }
}
