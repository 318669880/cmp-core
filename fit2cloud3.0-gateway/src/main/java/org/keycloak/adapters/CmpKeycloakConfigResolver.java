package org.keycloak.adapters;

import com.fit2cloud.commons.utils.BeanUtils;
import com.fit2cloud.commons.utils.GlobalConfigurations;
import com.fit2cloud.commons.utils.LogUtil;
import org.apache.commons.lang3.StringUtils;
import org.keycloak.adapters.spi.HttpFacade;
import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.keycloak.representations.adapters.config.AdapterConfig;

import java.net.URI;
import java.net.URLDecoder;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by liqiang on 2018/6/4.
 */
public class CmpKeycloakConfigResolver extends KeycloakSpringBootConfigResolver {

    private ConcurrentHashMap<String, KeycloakDeployment> keycloakDeploymentMap = new ConcurrentHashMap<>();

    private static AdapterConfig adapterConfig;

    public KeycloakDeployment resolve(HttpFacade.Request request) {
        URI requestUri = URI.create(request.getURI());
        String host = requestUri.getHost();
        if (keycloakDeploymentMap.get(host) == null) {
            if (!StringUtils.startsWithIgnoreCase(adapterConfig.getAuthServerUrl(), "http")) {
                AdapterConfig t = BeanUtils.copyBean(new AdapterConfig(), adapterConfig);

                String serverAddress;
                if (StringUtils.equals(requestUri.getPath(), "/")) {
                    serverAddress = request.getURI();
                } else {
                    try {
                        serverAddress = StringUtils.substringBefore(URLDecoder.decode(request.getURI(), "utf-8"), requestUri.getPath());
                    } catch (Exception e) {
                        LogUtil.error(e.getMessage(), e);
                        throw new RuntimeException(e);
                    }
                }
                String requestScheme = request.getHeader("X-Forwarded-Proto");
                if (StringUtils.isNotBlank(requestScheme) && !StringUtils.equalsIgnoreCase(requestScheme, requestUri.getScheme())) {
                    serverAddress = StringUtils.replace(serverAddress, requestUri.getScheme(), requestScheme, 1);
                }
                t.setAuthServerUrl(serverAddress + adapterConfig.getAuthServerUrl());
                KeycloakDeployment keycloakDeployment = KeycloakDeploymentBuilder.build(t);
                String keycloakServerAddress = GlobalConfigurations.getProperty("keycloak-server-address", String.class, "");
                if (StringUtils.isNotBlank(keycloakServerAddress)) {
                    String tokenUrl = keycloakDeployment.getTokenUrl();
                    tokenUrl = StringUtils.replaceAll(tokenUrl, StringUtils.substringBefore(tokenUrl, "/realms"), keycloakServerAddress);
                    keycloakDeployment.tokenUrl = tokenUrl;
                    String jwksUrl = keycloakDeployment.getJwksUrl();
                    jwksUrl = StringUtils.replaceAll(jwksUrl, StringUtils.substringBefore(jwksUrl, "/realms"), keycloakServerAddress);
                    keycloakDeployment.jwksUrl = jwksUrl;
                    String accountUrl = keycloakDeployment.getAccountUrl();
                    accountUrl = StringUtils.replaceAll(accountUrl, StringUtils.substringBefore(accountUrl, "/realms"), keycloakServerAddress);
                    keycloakDeployment.accountUrl = accountUrl;
                }
                keycloakDeploymentMap.put(host, keycloakDeployment);
            } else {
                keycloakDeploymentMap.put(host, KeycloakDeploymentBuilder.build(adapterConfig));
            }
        }
        return keycloakDeploymentMap.get(host);
    }

    public static void setAdapterConfig(AdapterConfig _adapterConfig) {
        adapterConfig = _adapterConfig;
    }

    public static AdapterConfig getAdapterConfig() {
        return adapterConfig;
    }
}
