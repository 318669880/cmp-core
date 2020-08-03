package com.fit2cloud.common.web;

import com.fit2cloud.common.web.template.WebPublicConfig;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RequestMapping("/" + WebPublicConfig.WEB_PUBLIC_CONTEXT)
@RestController
@Configuration
public class WebPublicController {

    static final String WEB_PUBLIC_CONTEXT_PATH = WebPublicConfig.WEB_PUBLIC_CONTEXT + "/";

    @RequestMapping(value = "/**", method = RequestMethod.GET)
    public void getResources(HttpServletRequest request, HttpServletResponse response) {
        String pattern = (String)
                request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        if (pattern == null || pattern.length() == 0) {
            response.setStatus(400);
            return;
        }
        String resourceUri = new AntPathMatcher().extractPathWithinPattern(pattern,
                request.getServletPath());
        if (StringUtils.containsIgnoreCase(resourceUri, WEB_PUBLIC_CONTEXT_PATH)) {
            resourceUri = StringUtils.substringAfterLast(resourceUri, WEB_PUBLIC_CONTEXT_PATH);
        }
        if (StringUtils.isBlank(resourceUri)) {
            resourceUri = "index.html";
        }
        ResourceReader.read(WEB_PUBLIC_CONTEXT_PATH + resourceUri, request, response);
    }

}
