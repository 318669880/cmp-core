package com.fit2cloud.gateway.controller;


import com.fit2cloud.commons.server.constants.Lang;
import com.fit2cloud.commons.server.constants.WebConstants;
import com.fit2cloud.commons.server.i18n.Translator;
import com.fit2cloud.commons.server.security.SsoFilter;
import com.fit2cloud.commons.utils.LogUtil;
import com.fit2cloud.gateway.session.SessionManager;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

/**
 * Created by liqiang on 2018/6/7.
 */

@Controller
public class FacadeController {

    @Resource
    private DiscoveryClient discoveryClient;

    @Value("${spring.application.name}")
    private String appName;

    @Value("${cmp.default-module:dashboard}")
    private String defaultModule;

    private final String MODEL_PARAMETER = "_module";

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public Object index(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
        try {
            List<String> services = discoveryClient.getServices();
            if (CollectionUtils.isEmpty(services) || !services.contains(defaultModule)) {
                model.addAllAttributes(WebConstants.getErrorVariables("/", 503, "Service Unavailable"));
                return "web-public/error";
            }
            String module = defaultModule;
//            if (!services.contains(module)) {
//                // 如果没有默认模块，则随机取一个
//                for (String service : services) {
//                    if (!StringUtils.equalsIgnoreCase(appName, service)) {
//                        module = service;
//                        break;
//                    }
//                }
//            }
            String parameters = StringUtils.EMPTY;
            if (MapUtils.isNotEmpty(request.getParameterMap())) {
                for (String p : request.getParameterMap().keySet()) {
                    parameters = parameters + "&" + p + "=" + request.getParameter(p);
                }
            }
            if (StringUtils.isNotBlank(request.getParameter(MODEL_PARAMETER))) {
                module = request.getParameter(MODEL_PARAMETER);
            } else {
                if (!StringUtils.equalsIgnoreCase(module, "dashboard")) {
                    parameters = parameters + "&" + MODEL_PARAMETER + "=" + module;
                }
            }
            return "redirect:/" + module + "/" + (StringUtils.isNotBlank(parameters) ? "?" + parameters : "");
        } catch (Exception e) {
            response.setStatus(500);
            throw e;
        }
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public Object login(HttpServletRequest request, HttpServletResponse response, Model model) {
        model.addAttribute("timestamp", WebConstants.timestamp);
        if (SessionManager.getSessionUser(request) != null) {
            model.addAttribute("message", "USER_ALREADY_LOGIN");
        }
        if (ArrayUtils.isNotEmpty(request.getCookies())) {
            for (Cookie cookie : request.getCookies()) {
                if (StringUtils.equalsIgnoreCase(cookie.getName(), SsoFilter.SSO_ERROR_COOKIE_NAME)) {
                    if (StringUtils.isNotBlank(cookie.getValue())) {
                        try {
                            model.addAttribute("message", URLDecoder.decode(cookie.getValue(), "UTF-8"));
                        } catch (UnsupportedEncodingException e) {
                            LogUtil.getLogger().error("failed to decode sso error message.", e);
                        }
                        cookie.setValue(StringUtils.EMPTY);
                        cookie.setPath(WebConstants.ROOT_PATH);
                        cookie.setMaxAge(0);
                        response.addCookie(cookie);
                    }
                }
            }
        }
        Lang lang = Translator.getLang();
        Cookie keycloakLocaleCookie = new Cookie("KEYCLOAK_LOCALE", "en");
        keycloakLocaleCookie.setPath(WebConstants.ROOT_PATH);
        if (lang != null && lang == Lang.zh_CN) {
            keycloakLocaleCookie.setValue("zh-CN");
        }
        response.addCookie(keycloakLocaleCookie);
        return "web-public/login";
    }

    @RequestMapping(value = "/api-doc", method = RequestMethod.GET)
    public void apiDoc(HttpServletResponse response) {
        response.setHeader("Location", "/swagger-ui.html");
        response.setStatus(302);
    }

    @RequestMapping(value = "/swagger-ui.html", method = RequestMethod.GET)
    public Object apiDoc(HttpServletRequest request, HttpServletResponse response, Model model) {
        model.addAttribute("timestamp", WebConstants.timestamp);
        return "swagger-ui";
    }
}
