package org.keycloak.adapters.springboot;

import com.fit2cloud.commons.server.security.ApiKeyHandler;
import com.fit2cloud.commons.utils.CommonBeanFactory;
import com.fit2cloud.commons.utils.LogUtil;
import com.fit2cloud.gateway.keycloak.KeycloakExcules;
import com.fit2cloud.gateway.keycloak.KeycloakInternalExcules;
import com.fit2cloud.gateway.session.SessionManager;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.eclipse.jetty.security.ConstraintSecurityHandler;
import org.eclipse.jetty.server.Authentication;
import org.eclipse.jetty.server.Request;
import org.keycloak.adapters.OIDCAuthenticationError;
import org.keycloak.adapters.spi.AuthenticationError;

import javax.servlet.DispatcherType;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by liqiang on 2018/6/4.
 */
public class KeycloakSecurityHandler extends ConstraintSecurityHandler {

    public static List<String> excludes = new ArrayList<>();

    @Override
    public void handle(String pathInContext, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        boolean isLoginRequest = false;
        if (!needAuth(baseRequest)) {
            baseRequest.setDispatcherType(DispatcherType.REQUEST);
            baseRequest.setAuthentication(Authentication.UNAUTHENTICATED);
            if (StringUtils.equalsAnyIgnoreCase(request.getMethod(), "post") &&
                    StringUtils.endsWithIgnoreCase(pathInContext, "/login-actions/authenticate")) {
                isLoginRequest = true;
            }
        }
        if (Objects.nonNull(request.getAttribute(AuthenticationError.class.getName())) && request.getAttribute(AuthenticationError.class.getName()) instanceof OIDCAuthenticationError) {
            OIDCAuthenticationError error = (OIDCAuthenticationError) request.getAttribute(AuthenticationError.class.getName());
            if (OIDCAuthenticationError.Reason.INVALID_STATE_COOKIE.name().equals(error.getReason().name())) {
                response.setStatus(HttpStatus.SC_MOVED_TEMPORARILY);
                if (StringUtils.isNotBlank(baseRequest.getOriginalURI())) {
                    try {
                        URIBuilder uriBuilder = new URIBuilder(baseRequest.getOriginalURI());
                        List<NameValuePair> parameters = new ArrayList<>();
                        uriBuilder.getQueryParams().forEach(nameValuePair -> {
                            if (!StringUtils.equalsAnyIgnoreCase(nameValuePair.getName(), "state", "session_state", "code")) {
                                parameters.add(nameValuePair);
                            }
                        });
                        uriBuilder.clearParameters();
                        if (CollectionUtils.isNotEmpty(parameters)) {
                            uriBuilder.addParameters(parameters);
                        }
                        response.sendRedirect(uriBuilder.toString());
                    } catch (URISyntaxException e) {
                        LogUtil.error("Failed to parse " + baseRequest.getOriginalURI(), e);
                        response.sendRedirect("/");
                    }
                } else {
                    response.sendRedirect("/");
                }
            }
        }

        super.handle(pathInContext, baseRequest, request, response);
        SessionManager.setSessionUser(request);
        if (isLoginRequest) {
            SessionManager.logUserLogin(request, response);
        }
    }

    private boolean needAuth(Request baseRequest) {
        if (CollectionUtils.isEmpty(excludes)) {
            synchronized (excludes) {
                if (CollectionUtils.isEmpty(excludes)) {
                    excludes.addAll(CommonBeanFactory.getBean(KeycloakInternalExcules.class).getExcludes());
                    excludes.addAll(CommonBeanFactory.getBean(KeycloakExcules.class).getExcludes());
                }
            }
        }
        String path = baseRequest.getServletPath();
        if (StringUtils.isBlank(path) || StringUtils.equalsIgnoreCase(path, "/")) {
            return false;
        }
        for (String exclude : excludes) {
            if (StringUtils.containsIgnoreCase(path, StringUtils.replaceAll(exclude, "\\*", ""))) {
                return false;

            }
        }
        if (ApiKeyHandler.isApiKeyCall(baseRequest)) {
            return false;
        }
        return true;
    }

}
