package com.fit2cloud.commons.server.handle.annotation;

import com.fit2cloud.commons.server.constants.I18nConstants;

import java.lang.annotation.*;

@Documented
@Inherited
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface I18n {
    String value() default I18nConstants.LOCAL;
}
