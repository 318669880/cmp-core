package com.fit2cloud.gateway.filter;

import com.fit2cloud.commons.server.security.ApiKeyHandler;
import com.fit2cloud.commons.server.security.SsoSessionHandler;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by liqiang on 2018/6/7.
 */
public class ApiFilter extends ZuulFilter {

    private Logger logger = LoggerFactory.getLogger(ApiFilter.class);

    @Override
    public Object run() throws ZuulException {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        try {
            String userId = ApiKeyHandler.getUser(request);
            if (StringUtils.isNotBlank(userId)) {
                ctx.addZuulRequestHeader(SsoSessionHandler.SSO_HEADER_AUTH_NAME, SsoSessionHandler.generateId(userId));
                ctx.addZuulRequestHeader(HttpHeaders.ACCEPT, "application/json, text/plain, */*");
                ctx.addZuulRequestHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE);
                if (logger.isDebugEnabled()) {
                    logger.debug(userId + " accesses: " + request.getRequestURI() + " with api key");
                }
            }
        } catch (Exception e) {
            throw new ZuulException(e, 403, e.getMessage());
        }
        return null;
    }

    @Override
    public boolean shouldFilter() {
        return true;// 是否执行该过滤器，此处为true，说明需要过滤
    }

    @Override
    public int filterOrder() {
        return 1;// 优先级为0，数字越大，优先级越低
    }

    @Override
    public String filterType() {
        return "pre";// 前置过滤器
    }
}