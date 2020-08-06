package com.fit2cloud.gateway.keycloak;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(
        prefix = "keycloak-security-internal",
        ignoreUnknownFields = true
)
public class KeycloakInternalExcules {

    private List<String> excludes = new ArrayList<>();

    public List<String> getExcludes() {
        return excludes;
    }

    public void setExcludes(List<String> excludes) {
        this.excludes = excludes;
    }
}
