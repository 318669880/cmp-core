package com.fit2cloud.gateway.config;

import com.fit2cloud.gateway.filter.*;
import com.fit2cloud.gateway.keycloak.KeycloakExcules;
import com.fit2cloud.gateway.keycloak.KeycloakInternalExcules;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Created by liqiang on 2018/6/4.
 */
@Configuration
@PropertySource(value = {
        "file:/opt/fit2cloud/conf/fit2cloud.properties"
}, encoding = "UTF-8", ignoreResourceNotFound = true)
public class CommonConfig {

    @Bean
    public KeycloakInternalExcules keycloakInternalExcules() {
        return new KeycloakInternalExcules();
    }

    @Bean
    public KeycloakExcules keycloakExcules() {
        return new KeycloakExcules();
    }

    @Bean
    public SsoFilter ssoFilter() {
        return new SsoFilter();
    }

    @Bean
    public ApiFilter apiFilter() {
        return new ApiFilter();
    }

    @Bean
    public XForwardFilter xForwardFilter() {
        return new XForwardFilter();
    }

    @Bean
    public MethodFilter methodFilter() {
        return new MethodFilter();
    }

    @Bean
    public LogoutFilter logoutFilter() {
        return new LogoutFilter();
    }

}
