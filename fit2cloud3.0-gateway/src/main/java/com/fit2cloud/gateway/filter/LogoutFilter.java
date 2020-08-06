package com.fit2cloud.gateway.filter;

import com.fit2cloud.commons.server.constants.I18nConstants;
import com.fit2cloud.commons.server.security.SsoSessionHandler;
import com.fit2cloud.gateway.controller.LogoutController;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by liqiang on 2018/10/21.
 */
public class LogoutFilter extends ZuulFilter {

    private Logger logger = LoggerFactory.getLogger(LogoutFilter.class);

    @Override
    public Object run() throws ZuulException {
        try {
            RequestContext ctx = RequestContext.getCurrentContext();
            HttpServletRequest request = ctx.getRequest();
            HttpServletResponse response = ctx.getResponse();
            if (StringUtils.startsWithIgnoreCase(request.getRequestURI(), "/auth/")) {
                if (StringUtils.startsWithIgnoreCase(LogoutController.logoutUrl, request.getRequestURI())) {
                    SsoSessionHandler.logout(request, response, I18nConstants.LANG_COOKIE_NAME);
                }
            }
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public String filterType() {
        return "post";
    }

    @Override
    public int filterOrder() {
        return 100;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }
}
