package com.fit2cloud.commons.server.handle;

import com.alibaba.fastjson.JSON;
import com.fit2cloud.commons.server.constants.I18nConstants;
import com.fit2cloud.commons.server.handle.annotation.I18n;
import com.fit2cloud.commons.server.handle.annotation.NoResultHolder;
import com.fit2cloud.commons.server.i18n.Translator;
import com.fit2cloud.commons.utils.ResultHolder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * 统一处理返回结果集
 */
@RestControllerAdvice({"com.fit2cloud", "springfox.documentation.swagger2.web"})
public class ResultResponseBodyAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> converterType) {
        return MappingJackson2HttpMessageConverter.class.isAssignableFrom(converterType) || StringHttpMessageConverter.class.isAssignableFrom(converterType);
    }

    @Override
    public Object beforeBodyWrite(Object o, MethodParameter methodParameter, MediaType mediaType, Class<? extends HttpMessageConverter<?>> converterType, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        //swagger
        String path = serverHttpRequest.getURI().getPath();
        if (StringUtils.equalsIgnoreCase(path, "/v2/api-docs")) {
            return translate(o, I18nConstants.LOCAL);
        }


        //if true, need to translate
        if (methodParameter.hasMethodAnnotation(I18n.class)) {
            I18n i18n = methodParameter.getMethodAnnotation(I18n.class);
            o = translate(o, i18n.value());
        }

        if (methodParameter.hasMethodAnnotation(NoResultHolder.class)) {
            return o;
        }
        if (o == null && StringHttpMessageConverter.class.isAssignableFrom(converterType)) {
            return o;
        }

        if (!(o instanceof ResultHolder)) {
            if (o instanceof String) {
                return JSON.toJSONString(ResultHolder.success(o));
            }
            return ResultHolder.success(o);
        }
        return o;
    }

    // i18n
    private Object translate(Object obj, String type) {
        if (StringUtils.equalsIgnoreCase(type, I18nConstants.LOCAL)) {
            return Translator.gets(obj);
        }
        return Translator.clusterGets(obj);
    }


}
