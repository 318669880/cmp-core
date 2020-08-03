package com.fit2cloud.autoconfigure;

import com.fit2cloud.commons.server.constants.SessionConstants;
import com.fit2cloud.commons.server.constants.WebConstants;
import com.fit2cloud.commons.utils.GlobalConfigurations;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by liqiang on 2018/7/25.
 */
@Configuration
public class HttpCacheConfig implements WebMvcConfigurer {

    private static final String CACHE_CONTROL_10_YEARS = "max-age=" + String.valueOf(3600 * 24 * 30 * 12 * 10);

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HttpCacheIntercept()).addPathPatterns("/**");
    }

    private class HttpCacheIntercept implements HandlerInterceptor {

        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
            if (handler instanceof ResourceHttpRequestHandler) {
                if (GlobalConfigurations.isReleaseMode()) {
                    setMaxAge(request, response, true);
                }
            }
            return true;
        }

        @Override
        public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

        }

        @Override
        public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

        }
    }

    public static void setMaxAge(HttpServletRequest request, HttpServletResponse response, boolean forceDefault) {
        if (StringUtils.isNotBlank(response.getHeader(HttpHeaders.CACHE_CONTROL))) {
            return;
        }
        if (StringUtils.length(request.getParameter("t")) == 13 || StringUtils.length(request.getParameter("_t")) == 13) {
            response.setHeader(HttpHeaders.CACHE_CONTROL, CACHE_CONTROL_10_YEARS);
        } else {
            if (forceDefault) {
                response.setHeader(HttpHeaders.CACHE_CONTROL, "max-age=" + GlobalConfigurations.getProperty("resource.max-age", Long.class, SessionConstants.DEFAULT_RESOURCE_MAX_AGE));
            }
        }
        response.addDateHeader(HttpHeaders.LAST_MODIFIED, WebConstants.timestamp);
    }
}
