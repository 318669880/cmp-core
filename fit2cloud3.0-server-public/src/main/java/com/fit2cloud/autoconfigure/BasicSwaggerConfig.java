package com.fit2cloud.autoconfigure;

import com.fasterxml.classmate.TypeResolver;
import com.fit2cloud.commons.server.module.ServerInfo;
import com.fit2cloud.commons.server.swagger.F2cRequestHandlerSelectors;
import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import io.swagger.annotations.ApiOperation;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.async.DeferredResult;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.WildcardType;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.*;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.ArrayList;

import static springfox.documentation.schema.AlternateTypeRules.newRule;

@EnableSwagger2
@Configuration
@EnableKnife4j
@Import(BeanValidatorPluginsConfiguration.class)
public class BasicSwaggerConfig {

    @Resource
    private TypeResolver typeResolver;
    @Resource
    private ServerInfo serverInfo;

    private ApiInfo apiInfo() {
        Contact contact = new Contact("FIT2CLOUD", null, null);
        return new ApiInfoBuilder()
                .title("FIT2CLOUD Restful APIs")
                .contact(contact)
                .version("2.0")
                .build();
    }

    @Bean
    @ConditionalOnMissingBean
    public Docket docket() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
                .apis(F2cRequestHandlerSelectors.withMethodAnnotationModules(serverInfo.getModule().getId()))
                .paths(PathSelectors.any())
                .build()
                .directModelSubstitute(LocalDate.class, String.class)
                .genericModelSubstitutes(ResponseEntity.class)
                .alternateTypeRules(
                        newRule(typeResolver.resolve(DeferredResult.class,
                                typeResolver.resolve(ResponseEntity.class, WildcardType.class)),
                                typeResolver.resolve(WildcardType.class)))
                .useDefaultResponseMessages(false)
                .enableUrlTemplating(true);
    }

    @Bean
    @ConditionalOnMissingBean
    public SwaggerResourcesProvider swaggerResourcesProvider(InMemorySwaggerResourcesProvider defaultResourcesProvider) {
        return () -> new ArrayList<>(defaultResourcesProvider.get());
    }


    @Bean
    @ConditionalOnMissingBean
    UiConfiguration uiConfig() {
        return UiConfigurationBuilder.builder()
                .deepLinking(true)
                .displayOperationId(false)
                .defaultModelsExpandDepth(1)
                .defaultModelExpandDepth(1)
                .defaultModelRendering(ModelRendering.EXAMPLE)
                .displayRequestDuration(true)
                .docExpansion(DocExpansion.NONE)
                .filter(false)
                .maxDisplayedTags(null)
                .operationsSorter(OperationsSorter.ALPHA)
                .showExtensions(false)
                .tagsSorter(TagsSorter.ALPHA)
                .supportedSubmitMethods(UiConfiguration.Constants.DEFAULT_SUBMIT_METHODS)
                .validatorUrl(null)
                .build();
    }
}
