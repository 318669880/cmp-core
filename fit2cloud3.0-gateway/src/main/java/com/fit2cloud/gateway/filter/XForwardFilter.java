package com.fit2cloud.gateway.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by liqiang on 2018/6/15.
 */
public class XForwardFilter extends ZuulFilter {
    private Logger logger = LoggerFactory.getLogger(XForwardFilter.class);

    @Override
    public boolean shouldFilter() {
        return true;// 是否执行该过滤器，此处为true，说明需要过滤
    }

    @Override
    public int filterOrder() {
        return 6;// 优先级为0，数字越大，优先级越低
    }

    @Override
    public String filterType() {
        return "pre";// 前置过滤器
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        if (this.hasHeader(request, "X-Forwarded-Host")) {
            if (logger.isTraceEnabled()) {
                logger.trace("adding header X-Forwarded-Host: " + request.getHeader("X-Forwarded-Host"));
            }
            ctx.addZuulRequestHeader("X-Forwarded-Host", request.getHeader("X-Forwarded-Host"));
        }

        if (this.hasHeader(request, "X-Forwarded-Port")) {
            if (logger.isTraceEnabled()) {
                logger.trace("adding header X-Forwarded-Port: " + request.getHeader("X-Forwarded-Port"));
            }
            ctx.addZuulRequestHeader("X-Forwarded-Port", request.getHeader("X-Forwarded-Port"));
        }

        if (this.hasHeader(request, "X-Forwarded-Proto")) {
            if (logger.isTraceEnabled()) {
                logger.trace("adding header X-Forwarded-Proto: " + request.getHeader("X-Forwarded-Proto"));
            }
            ctx.addZuulRequestHeader("X-Forwarded-Proto", request.getHeader("X-Forwarded-Proto"));
        }
        return null;
    }

    private boolean hasHeader(HttpServletRequest request, String name) {
        return StringUtils.hasLength(request.getHeader(name));
    }
}
