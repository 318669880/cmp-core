package com.fit2cloud.commons.server.security;

import com.fit2cloud.commons.server.constants.WebConstants;
import com.fit2cloud.commons.utils.GlobalConfigurations;
import com.fit2cloud.commons.utils.LogUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.web.filter.authc.AnonymousFilter;
import org.apache.shiro.web.util.WebUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;

/**
 * Created by liqiang on 2018/6/7.
 */
public class SsoFilter extends AnonymousFilter {

    public static final String SSO_ERROR_COOKIE_NAME = "rememberme_sso_error";

    @Override
    protected boolean onPreHandle(ServletRequest request, ServletResponse response, Object mappedValue) {
        try {
            if (!SecurityUtils.getSubject().isAuthenticated()) {
                String userId = SsoSessionHandler.validate(WebUtils.toHttp(request));
                if (StringUtils.isNotBlank(userId)) {
                    if (LogUtil.getLogger().isDebugEnabled()) {
                        LogUtil.getLogger().debug("user sso auth: " + userId);
                    }
                    SecurityUtils.getSubject().login(new UsernamePasswordToken(userId, SsoSessionHandler.random));
                }
            } else {
                if (ApiKeyHandler.isApiKeyCall(WebUtils.toHttp(request))) {
                    String userId = SsoSessionHandler.validate(WebUtils.toHttp(request));
                    SecurityUtils.getSubject().login(new UsernamePasswordToken(userId, SsoSessionHandler.random));
                }
            }

            if (!SecurityUtils.getSubject().isAuthenticated()) {
                ((HttpServletResponse) response).setHeader("Authentication-Status", "invalid");
            }
        } catch (Exception e) {
            if (ApiKeyHandler.isApiKeyCall(WebUtils.toHttp(request))) {
                throw e;
            }
            LogUtil.getLogger().error("failed to handle single sign on..", e);
            if (GlobalConfigurations.isReleaseMode()) {
                try {
                    if (StringUtils.isNotBlank(e.getMessage())) {
                        Cookie cookie = new Cookie(SSO_ERROR_COOKIE_NAME, URLEncoder.encode(e.getMessage(), "UTF-8"));
                        cookie.setPath(WebConstants.ROOT_PATH);
                        cookie.setMaxAge(30);
                        WebUtils.toHttp(response).addCookie(cookie);
                    }
                    WebUtils.issueRedirect(request, response, "/logout");
                } catch (IOException ioException) {
                    LogUtil.getLogger().error("failed to redirect.", ioException);
                }
            }
        }

        return true;
    }
}