package org.keycloak.adapters.springboot;

import org.eclipse.jetty.security.ConstraintSecurityHandler;
import org.eclipse.jetty.security.SecurityHandler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.keycloak.adapters.CmpKeycloakConfigResolver;
import org.keycloak.adapters.jetty.KeycloakJettyAuthenticator;
import org.springframework.boot.web.embedded.jetty.JettyServerCustomizer;

/**
 * Created by liqiang on 2018/6/1.
 */
public class KeycloakJettyServerCustomizer implements JettyServerCustomizer {

    protected KeycloakSpringBootProperties keycloakProperties;

    public KeycloakJettyServerCustomizer(KeycloakSpringBootProperties keycloakProperties) {
        this.keycloakProperties = keycloakProperties;
    }

    public void customize(Server server) {

        WebAppContext webAppContext = server.getBean(WebAppContext.class);
        if (webAppContext == null) {
            webAppContext = (WebAppContext) server.getHandler();
        }
        KeycloakSecurityHandler keycloakSecurityHandler = new KeycloakSecurityHandler();
        if (webAppContext.getSecurityHandler() instanceof ConstraintSecurityHandler) {
            keycloakSecurityHandler.setConstraintMappings(((ConstraintSecurityHandler) webAppContext.getSecurityHandler()).getConstraintMappings());
        }
        webAppContext.setSecurityHandler(keycloakSecurityHandler);
        keycloakSecurityHandler.setAuthenticator(new KeycloakJettyAuthenticator());
        SecurityHandler s = webAppContext.getSecurityHandler();
        if (s.getAuthenticator() instanceof KeycloakJettyAuthenticator) {
            CmpKeycloakConfigResolver cmpKeycloakConfigResolver = new CmpKeycloakConfigResolver();
            cmpKeycloakConfigResolver.setAdapterConfig(this.keycloakProperties);
            ((KeycloakJettyAuthenticator) s.getAuthenticator()).setConfigResolver(cmpKeycloakConfigResolver);
        }
    }
}