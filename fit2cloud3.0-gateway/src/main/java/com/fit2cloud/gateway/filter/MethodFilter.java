package com.fit2cloud.gateway.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by liqiang on 2018/10/21.
 */
public class MethodFilter extends ZuulFilter {

    private Logger logger = LoggerFactory.getLogger(MethodFilter.class);

    @Override
    public Object run() throws ZuulException {
        try {
            RequestContext ctx = RequestContext.getCurrentContext();
            HttpServletRequest request = ctx.getRequest();
            if (StringUtils.equalsIgnoreCase(request.getMethod(), "TRACE")) {
                Exception e = new Exception("Method not allowed");
                throw new ZuulException(e, 405, e.getMessage());
            }
            if (StringUtils.equalsIgnoreCase(request.getMethod(), "OPTIONS")) {
                ctx.addZuulResponseHeader("Allow", "GET, HEAD, POST, PUT, OPTIONS");
            }
            ctx.addZuulResponseHeader("Server", null);
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
