package com.fit2cloud.commons.server.security;

import com.fit2cloud.commons.server.constants.WebConstants;
import com.fit2cloud.commons.utils.EncryptUtils;
import com.fit2cloud.commons.utils.GlobalConfigurations;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Created by liqiang on 2018/6/7.
 */
public class SsoSessionHandler {

    private static Logger logger = LoggerFactory.getLogger(SsoSessionHandler.class);

    public static final String SSO_HEADER_AUTH_NAME = "FIT2CLOUD_X_AUTH";

    public static final String SSO_SOURCE_ID = "sourceId";

    public static String random = UUID.randomUUID().toString() + UUID.randomUUID().toString();

    public static String generateId(String authInfo) {
        return SessionGenerator.generateId(authInfo);
    }

    public static String validate(HttpServletRequest request) {
        try {
            String v = request.getHeader(SSO_HEADER_AUTH_NAME);
            if (StringUtils.isNotBlank(v)) {
                return SessionGenerator.fromId(v);
            }
        } catch (Exception e) {
            logger.error("failed to validate", e);
        }

        return null;
    }


    public static void logout(HttpServletRequest request, HttpServletResponse response, String... remainSessionId) {
        try {
            Set<String> remainSessionIdSet = new HashSet();
            int len$;
            int i$;
            if (remainSessionId != null && remainSessionId.length > 0) {
                String[] arr$ = remainSessionId;
                len$ = remainSessionId.length;

                for (i$ = 0; i$ < len$; ++i$) {
                    String s = arr$[i$];
                    if (s != null && !"".equals(s)) {
                        remainSessionIdSet.add(s.toLowerCase());
                    }
                }
            }

            if (request.getCookies() != null) {
                Cookie[] arr$ = request.getCookies();
                len$ = arr$.length;

                for (i$ = 0; i$ < len$; ++i$) {
                    Cookie cookie = arr$[i$];
                    if (!cookie.getName().toLowerCase().contains("rememberme") && (remainSessionIdSet.size() <= 0 || !remainSessionIdSet.contains(cookie.getName().toLowerCase()))) {
                        cookie.setValue("deleteMe");
                        cookie.setPath(WebConstants.ROOT_PATH);
                        cookie.setMaxAge(0);
                        response.addCookie(cookie);
                    }
                }
            } else {
                Cookie cookie = new Cookie(GlobalConfigurations.getCookieName(), "deleteMe");
                cookie.setPath(WebConstants.ROOT_PATH);
                cookie.setMaxAge(0);
                response.addCookie(cookie);
            }
            request.logout();
        } catch (Exception var8) {
            logger.error("failed to logout", var8);
        }

    }

    public static class SessionGenerator {
        public SessionGenerator() {
        }

        public static String generateId(String authInfo) {
            return EncryptUtils.aesEncrypt(parse2Str(authInfo)).toString();
        }

        public static String fromId(String sessionId) {
            String authInfoString = EncryptUtils.aesDecrypt(sessionId).toString();
            return fromStr(authInfoString);
        }

        private static String parse2Str(String authInfo) {
            return UUID.randomUUID().toString() + "|" + authInfo + "|" + System.currentTimeMillis();
        }

        private static String fromStr(String authInfoString) {
            String[] sp = authInfoString.split("\\|");
            return sp[1];
        }
    }
}
