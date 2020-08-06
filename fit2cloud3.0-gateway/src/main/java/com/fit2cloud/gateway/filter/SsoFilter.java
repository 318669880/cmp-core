package com.fit2cloud.gateway.filter;

import com.fit2cloud.commons.server.security.SsoSessionHandler;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by liqiang on 2018/6/7.
 */
public class SsoFilter extends ZuulFilter {

    private Logger logger = LoggerFactory.getLogger(SsoFilter.class);

    @Override
    public Object run() throws ZuulException {
        try {
            String userName;
            RequestContext ctx = RequestContext.getCurrentContext();
            ctx.addZuulRequestHeader(SsoSessionHandler.SSO_HEADER_AUTH_NAME, StringUtils.EMPTY);
            HttpServletRequest request = ctx.getRequest();
            if (request.getUserPrincipal() != null && StringUtils.isNotBlank(request.getUserPrincipal().getName())) {
                String authValue;
                userName = request.getUserPrincipal().getName();
                Object authObject = request.getSession(true).getAttribute(SsoSessionHandler.SSO_HEADER_AUTH_NAME);
                if (authObject != null && StringUtils.isNotBlank(authObject.toString())) {
                    authValue = authObject.toString();
                } else {
                    authValue = SsoSessionHandler.generateId(userName);
                    request.getSession().setAttribute(SsoSessionHandler.SSO_HEADER_AUTH_NAME, authValue);
                }
                ctx.addZuulRequestHeader(SsoSessionHandler.SSO_HEADER_AUTH_NAME, authValue);
                if (logger.isTraceEnabled()) {
                    logger.trace(userName + " accesses: " + request.getRequestURI());
                }
            }else {
                if (logger.isTraceEnabled()) {
                    logger.trace("anonymous accesses: " + request.getRequestURI());
                }
            }
        } catch (Exception e) {
            throw new ZuulException(e, 500, e.getMessage());
        }
        return null;
    }

    @Override
    public boolean shouldFilter() {
        return true;// 是否执行该过滤器，此处为true，说明需要过滤
    }

    @Override
    public int filterOrder() {
        return 0;// 优先级为0，数字越大，优先级越低
    }

    @Override
    public String filterType() {
        return "pre";// 前置过滤器
    }
}