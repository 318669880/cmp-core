package org.keycloak.adapters.springboot;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.embedded.jetty.JettyServerCustomizer;
import org.springframework.boot.web.embedded.jetty.JettyServletWebServerFactory;
import org.springframework.boot.web.embedded.tomcat.TomcatContextCustomizer;
import org.springframework.boot.web.embedded.undertow.UndertowDeploymentInfoCustomizer;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnWebApplication
@EnableConfigurationProperties({KeycloakSpringBootProperties.class})
@ConditionalOnProperty(
        value = {"keycloak.enabled"},
        matchIfMissing = true
)
public class KeycloakConfiguration extends KeycloakBaseSpringBootConfiguration {
    public KeycloakConfiguration() {
    }

    @Bean
    public WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> getKeycloakContainerCustomizer() {
        return new WebServerFactoryCustomizer<ConfigurableServletWebServerFactory>() {
            public void customize(ConfigurableServletWebServerFactory configurableServletWebServerFactory) {
                JettyServletWebServerFactory containerx = (JettyServletWebServerFactory) configurableServletWebServerFactory;
                containerx.addServerCustomizers(new JettyServerCustomizer[]{KeycloakConfiguration.this.jettyKeycloakServerCustomizer()});
                containerx.addServerCustomizers(new KeycloakJettyServerCustomizer(KeycloakConfiguration.this.keycloakProperties));
            }
        };
    }

    @Bean
    @ConditionalOnClass(
            name = {"org.eclipse.jetty.webapp.WebAppContext"}
    )
    public JettyServerCustomizer jettyKeycloakServerCustomizer() {
        return new KeycloakAutoConfiguration.KeycloakJettyServerCustomizer(this.keycloakProperties);
    }
}
