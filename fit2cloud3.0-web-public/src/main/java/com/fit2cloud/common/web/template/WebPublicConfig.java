package com.fit2cloud.common.web.template;


import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.thymeleaf.templateresolver.ITemplateResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by liqiang on 2018/6/13.
 */
@Configuration
@EnableConfigurationProperties({ThymeleafProperties.class})
public class WebPublicConfig implements WebMvcConfigurer {
    private ApplicationContext applicationContext;

    @Autowired
    private ThymeleafProperties properties;

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Bean
    @DependsOn("globalConfigurations")
    public ITemplateResolver webPublicTemplateResolver() {
        WebPublicResourceTemplateResolver templateResolver
                = new WebPublicResourceTemplateResolver();
        templateResolver.setApplicationContext(applicationContext);
        templateResolver.setPrefix(this.properties.getPrefix());
        templateResolver.setSuffix(this.properties.getSuffix());
        templateResolver.setTemplateMode(this.properties.getMode());
        templateResolver.setCacheable(this.properties.isCache());
        //templateResolver.setCacheable(false);
        return templateResolver;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new ThymeleafModelInterceptor()).addPathPatterns("/**");
    }

    private class ThymeleafModelInterceptor implements HandlerInterceptor {

        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
            return true;
        }

        @Override
        public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
            if (modelAndView != null) {
                if (StringUtils.isNotBlank(modelAndView.getViewName()) && modelAndView.getViewName().startsWith("redirect:")) {
                    return;
                }
                modelAndView.addAllObjects(WebPublicConfig.getUiConfiguration());
            }
        }

        @Override
        public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

        }
    }

    public static final String WEB_PUBLIC_CONTEXT = "web-public";

    public static final long timestamp = System.currentTimeMillis();

    public static Map<String, Object> getUiConfiguration() {
        Map<String, Object> result = new HashMap<>();
        result.put("timestamp", String.valueOf(timestamp));
        try {
            if (Class.forName("com.fit2cloud.commons.server.constants.WebConstants") != null) {
                result = com.fit2cloud.commons.server.constants.WebConstants.getUiConfiguration();
            }
        } catch (ClassNotFoundException e) {
        }
        return result;
    }

    public static Map<String, Object> getErrorVariables(String path, Integer httpStatus, String error) {
        Map<String, Object> result = new HashMap<>();
        try {
            if (Class.forName("com.fit2cloud.commons.server.constants.WebConstants") != null) {
                result = com.fit2cloud.commons.server.constants.WebConstants.getErrorVariables(path, httpStatus, error);
            }
        } catch (ClassNotFoundException e) {
        }
        return result;
    }
}
