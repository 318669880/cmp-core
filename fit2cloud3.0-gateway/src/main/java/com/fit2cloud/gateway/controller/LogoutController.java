package com.fit2cloud.gateway.controller;

import com.fit2cloud.commons.server.constants.I18nConstants;
import com.fit2cloud.commons.server.security.SsoSessionHandler;
import com.fit2cloud.commons.utils.LogUtil;
import com.fit2cloud.gateway.session.SessionManager;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.server.Request;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URI;
import java.net.URLEncoder;
import java.security.Principal;

@Controller
public class LogoutController {

    @Value("${keycloak.realm}")
    private String keycloakRealm;

    @Value("${keycloak.auth-server-url}")
    private String keycloakAuthServerUrl;

    public static String logoutUrl;

    @PostConstruct
    public void setLogoutUrl() {
        logoutUrl = StringUtils.replaceAll(keycloakAuthServerUrl + "/realms/" + keycloakRealm + "/protocol/openid-connect/logout?redirect_uri=", "//", "/");
    }

    @RequestMapping(value = "logout", method = RequestMethod.GET)
    public void logout(HttpServletRequest request, HttpServletResponse response, Principal principal) {
        try {
            SessionManager.logUserLogout(request);
            SsoSessionHandler.logout(request, response, I18nConstants.LANG_COOKIE_NAME);
            Request t = Request.getBaseRequest(request);
            String redirectUrl = StringUtils.substringBefore(t.getRequestURL().toString(), URI.create(t.getRequestURL().toString()).getPath()) + "/";
            response.sendRedirect(StringUtils.replaceAll(logoutUrl + URLEncoder.encode(redirectUrl, "UTF-8"), "//", "/"));
        } catch (Exception e) {
            LogUtil.error("failed to logout", e);
            response.setStatus(500);
        }
    }
}
