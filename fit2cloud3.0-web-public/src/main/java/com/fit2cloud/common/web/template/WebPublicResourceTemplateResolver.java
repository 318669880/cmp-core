package com.fit2cloud.common.web.template;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.templateresource.ITemplateResource;

import java.util.Map;

/**
 * Created by liqiang on 2018/6/12.
 */
public class WebPublicResourceTemplateResolver extends SpringResourceTemplateResolver {

    private ApplicationContext applicationContext = null;

    public WebPublicResourceTemplateResolver() {
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    protected ITemplateResource computeTemplateResource(IEngineConfiguration configuration, String ownerTemplate, String template, String resourceName, String characterEncoding, Map<String, Object> templateResolutionAttributes) {
        ITemplateResource templateResource = new WebPublicTemplateResource(this.applicationContext, resourceName);
        return templateResource;
    }


}
