package com.fit2cloud.gateway.config;

import com.fit2cloud.commons.server.i18n.Translator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;

import javax.annotation.Resource;
import java.util.*;

@Component
@Primary
public class DocumentationConfig implements SwaggerResourcesProvider {
    private final String SERVER_NAME = "server-name";
    private final String ENABLE_SWAGGER = "enable-swagger";
    @Resource
    private DiscoveryClient discoveryClient;
    @Value("${spring.application.name}")
    private String appName;

    @Override
    public List<SwaggerResource> get() {
        List<SwaggerResource> resources = new ArrayList<>();
        discoveryClient.getServices().stream()
                .filter(service -> !service.equalsIgnoreCase(appName))
                .filter(service -> getServiceMetadata(service)
                        .getOrDefault(ENABLE_SWAGGER, "false").equalsIgnoreCase("true"))
                .forEach(service ->
                        resources.add(swaggerResource(getServiceMetadata(service)
                                .getOrDefault(SERVER_NAME, service), getLocation(service))));
        return resources;
    }

    private SwaggerResource swaggerResource(String name, String location) {
        SwaggerResource swaggerResource = new SwaggerResource();
        swaggerResource.setName(Translator.clusterGet(name));
        swaggerResource.setLocation(location);
        swaggerResource.setSwaggerVersion("2.0");
        return swaggerResource;
    }

    private Map<String, String> getServiceMetadata(String service) {
        return Optional.ofNullable(discoveryClient.getInstances(service).get(0).getMetadata()).orElse(new HashMap<>());
    }

    private String getLocation(String service) {
        return "/" + service + "/v2/api-docs";
    }
}